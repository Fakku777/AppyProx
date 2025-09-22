const axios = require('axios');
const cheerio = require('cheerio');
const fs = require('fs');
const path = require('path');

/**
 * Scrapes Minecraft Wiki for game data and caches results
 */
class WikiScraper {
  constructor(config, logger) {
    this.config = config;
    this.logger = logger.child('WikiScraper');
    
    this.baseUrl = 'https://minecraft.fandom.com/wiki/';
    this.cacheDir = path.join(__dirname, '../../cache/wiki');
    this.cacheDuration = config.wiki_cache_duration || 86400000; // 24 hours default
    
    // Cache storage
    this.resourceCache = new Map();
    this.recipeCache = new Map();
    this.itemCache = new Map();
    
    this.initialized = false;
  }

  async initialize() {
    if (this.initialized) return;

    this.logger.info('Initializing wiki scraper...');
    
    // Ensure cache directory exists
    if (!fs.existsSync(this.cacheDir)) {
      fs.mkdirSync(this.cacheDir, { recursive: true });
    }

    // Load cached data
    await this.loadCachedData();
    
    this.initialized = true;
    this.logger.info('Wiki scraper initialized');
  }

  async loadCachedData() {
    try {
      const cacheFiles = ['resources.json', 'recipes.json', 'items.json'];
      
      for (const file of cacheFiles) {
        const filePath = path.join(this.cacheDir, file);
        if (fs.existsSync(filePath)) {
          const data = JSON.parse(fs.readFileSync(filePath, 'utf8'));
          
          // Check if cache is still valid
          if (Date.now() - data.timestamp < this.cacheDuration) {
            switch (file) {
              case 'resources.json':
                this.resourceCache = new Map(data.entries);
                break;
              case 'recipes.json':
                this.recipeCache = new Map(data.entries);
                break;
              case 'items.json':
                this.itemCache = new Map(data.entries);
                break;
            }
            this.logger.debug(`Loaded cached ${file}`);
          }
        }
      }
    } catch (error) {
      this.logger.warn('Failed to load cached wiki data:', error.message);
    }
  }

  async saveCachedData() {
    try {
      const cacheData = [
        { file: 'resources.json', cache: this.resourceCache },
        { file: 'recipes.json', cache: this.recipeCache },
        { file: 'items.json', cache: this.itemCache }
      ];

      for (const { file, cache } of cacheData) {
        const filePath = path.join(this.cacheDir, file);
        const data = {
          timestamp: Date.now(),
          entries: Array.from(cache.entries())
        };
        fs.writeFileSync(filePath, JSON.stringify(data, null, 2));
      }
      
      this.logger.debug('Cached wiki data saved');
    } catch (error) {
      this.logger.error('Failed to save cached wiki data:', error.message);
    }
  }

  async getResourceInfo(resourceName) {
    // Check cache first
    if (this.resourceCache.has(resourceName)) {
      return this.resourceCache.get(resourceName);
    }

    this.logger.debug(`Fetching resource info for: ${resourceName}`);

    try {
      const resourceData = await this.scrapeResourceData(resourceName);
      if (resourceData) {
        this.resourceCache.set(resourceName, resourceData);
        await this.saveCachedData();
      }
      return resourceData;
    } catch (error) {
      this.logger.error(`Failed to fetch resource info for ${resourceName}:`, error.message);
      return null;
    }
  }

  async scrapeResourceData(resourceName) {
    const url = `${this.baseUrl}${encodeURIComponent(resourceName)}`;
    
    try {
      const response = await axios.get(url, {
        timeout: 10000,
        headers: {
          'User-Agent': 'AppyProx/1.0.0 (Minecraft Automation Bot)'
        }
      });

      const $ = cheerio.load(response.data);
      
      // Extract resource information
      const resourceData = {
        name: resourceName,
        type: this.extractResourceType($),
        rarity: this.extractRarity($),
        stackSize: this.extractStackSize($),
        gatheringMethods: this.extractGatheringMethods($),
        locations: this.extractLocations($),
        tools: this.extractBestTools($),
        uses: this.extractUses($),
        lastUpdated: Date.now()
      };

      return resourceData;
    } catch (error) {
      this.logger.warn(`Could not scrape data for ${resourceName}:`, error.message);
      return this.getFallbackResourceData(resourceName);
    }
  }

  extractResourceType($) {
    // Try to determine resource type from categories or infobox
    const categories = $('.mw-normal-catlinks a').map((i, el) => $(el).text()).get();
    
    if (categories.some(cat => cat.includes('Ore'))) return 'ore';
    if (categories.some(cat => cat.includes('Block'))) return 'block';
    if (categories.some(cat => cat.includes('Item'))) return 'item';
    if (categories.some(cat => cat.includes('Food'))) return 'food';
    
    return 'unknown';
  }

  extractRarity($) {
    const infobox = $('.infobox');
    const rarityRow = infobox.find('tr').filter((i, el) => {
      return $(el).find('th').text().toLowerCase().includes('rarity');
    });
    
    if (rarityRow.length > 0) {
      return rarityRow.find('td').text().trim().toLowerCase();
    }
    
    return 'common';
  }

  extractStackSize($) {
    const infobox = $('.infobox');
    const stackRow = infobox.find('tr').filter((i, el) => {
      return $(el).find('th').text().toLowerCase().includes('stackable');
    });
    
    if (stackRow.length > 0) {
      const stackText = stackRow.find('td').text().trim();
      const match = stackText.match(/(\d+)/);
      return match ? parseInt(match[1]) : 64;
    }
    
    return 64; // Default stack size
  }

  extractGatheringMethods($) {
    const methods = [];
    
    // Look for mining information
    const miningSection = $('h2, h3').filter((i, el) => {
      return $(el).text().toLowerCase().includes('obtain');
    }).next('div, p, ul').first();
    
    if (miningSection.length > 0) {
      const text = miningSection.text().toLowerCase();
      
      if (text.includes('mining')) {
        methods.push({
          method: 'mining',
          efficiency: this.calculateMiningEfficiency(text),
          requirements: this.extractMiningRequirements(text)
        });
      }
      
      if (text.includes('crafting')) {
        methods.push({
          method: 'crafting',
          efficiency: 'high',
          requirements: ['crafting_table']
        });
      }
      
      if (text.includes('trading')) {
        methods.push({
          method: 'trading',
          efficiency: 'medium',
          requirements: ['emeralds', 'villager']
        });
      }
    }
    
    return methods.length > 0 ? methods : [{ method: 'unknown', efficiency: 'low', requirements: [] }];
  }

  calculateMiningEfficiency(text) {
    if (text.includes('diamond')) return 'very_high';
    if (text.includes('gold')) return 'high';
    if (text.includes('iron')) return 'medium';
    if (text.includes('stone')) return 'low';
    return 'medium';
  }

  extractMiningRequirements(text) {
    const requirements = [];
    
    if (text.includes('diamond pickaxe')) requirements.push('diamond_pickaxe');
    else if (text.includes('iron pickaxe')) requirements.push('iron_pickaxe');
    else if (text.includes('stone pickaxe')) requirements.push('stone_pickaxe');
    else if (text.includes('pickaxe')) requirements.push('pickaxe');
    
    if (text.includes('silk touch')) requirements.push('silk_touch');
    if (text.includes('fortune')) requirements.push('fortune');
    
    return requirements;
  }

  extractLocations($) {
    const locations = [];
    
    // Look for distribution/generation information
    const sections = $('h2, h3').filter((i, el) => {
      const text = $(el).text().toLowerCase();
      return text.includes('natural') || text.includes('generation') || text.includes('distribution');
    });
    
    sections.each((i, section) => {
      const content = $(section).nextUntil('h1, h2, h3').text().toLowerCase();
      
      if (content.includes('overworld')) locations.push('overworld');
      if (content.includes('nether')) locations.push('nether');
      if (content.includes('end')) locations.push('end');
      if (content.includes('village')) locations.push('village');
      if (content.includes('dungeon')) locations.push('dungeon');
    });
    
    return locations.length > 0 ? locations : ['overworld'];
  }

  extractBestTools($) {
    const tools = [];
    
    const toolSection = $('h2, h3').filter((i, el) => {
      return $(el).text().toLowerCase().includes('breaking');
    }).next().text().toLowerCase();
    
    if (toolSection.includes('pickaxe')) tools.push('pickaxe');
    if (toolSection.includes('shovel')) tools.push('shovel');
    if (toolSection.includes('axe')) tools.push('axe');
    if (toolSection.includes('sword')) tools.push('sword');
    if (toolSection.includes('shears')) tools.push('shears');
    
    return tools;
  }

  extractUses($) {
    const uses = [];
    
    const usageSection = $('h2, h3').filter((i, el) => {
      const text = $(el).text().toLowerCase();
      return text.includes('usage') || text.includes('crafting');
    });
    
    usageSection.each((i, section) => {
      const content = $(section).nextUntil('h1, h2, h3').find('a[title]').map((j, link) => {
        return $(link).attr('title');
      }).get();
      
      uses.push(...content);
    });
    
    return [...new Set(uses)]; // Remove duplicates
  }

  async getCraftingRecipe(itemName) {
    // Check cache first
    if (this.recipeCache.has(itemName)) {
      return this.recipeCache.get(itemName);
    }

    this.logger.debug(`Fetching crafting recipe for: ${itemName}`);

    try {
      const recipeData = await this.scrapeCraftingRecipe(itemName);
      if (recipeData) {
        this.recipeCache.set(itemName, recipeData);
        await this.saveCachedData();
      }
      return recipeData;
    } catch (error) {
      this.logger.error(`Failed to fetch recipe for ${itemName}:`, error.message);
      return null;
    }
  }

  async scrapeCraftingRecipe(itemName) {
    const url = `${this.baseUrl}${encodeURIComponent(itemName)}`;
    
    try {
      const response = await axios.get(url, {
        timeout: 10000,
        headers: {
          'User-Agent': 'AppyProx/1.0.0 (Minecraft Automation Bot)'
        }
      });

      const $ = cheerio.load(response.data);
      
      // Look for crafting recipe table
      const craftingTable = $('.crafting-recipe, .recipe').first();
      
      if (craftingTable.length > 0) {
        const recipe = {
          item: itemName,
          type: 'crafting',
          materials: this.extractRecipeMaterials(craftingTable, $),
          output: this.extractRecipeOutput(craftingTable, $),
          workstation: 'crafting_table',
          lastUpdated: Date.now()
        };
        
        return recipe;
      }
      
      // Look for smelting recipe
      const smeltingTable = $('.smelting-recipe, .furnace-recipe').first();
      if (smeltingTable.length > 0) {
        return {
          item: itemName,
          type: 'smelting',
          materials: this.extractSmeltingMaterials(smeltingTable, $),
          output: { item: itemName, quantity: 1 },
          workstation: 'furnace',
          lastUpdated: Date.now()
        };
      }
      
      return null;
    } catch (error) {
      this.logger.warn(`Could not scrape recipe for ${itemName}:`, error.message);
      return this.getFallbackRecipe(itemName);
    }
  }

  extractRecipeMaterials(table, $) {
    const materials = {};
    
    // Extract materials from crafting grid
    table.find('.ingredienttable td, .crafting-recipe-item').each((i, cell) => {
      const link = $(cell).find('a').first();
      if (link.length > 0) {
        const material = link.attr('title') || link.text().trim();
        if (material && material !== itemName) {
          materials[material] = (materials[material] || 0) + 1;
        }
      }
    });
    
    return materials;
  }

  extractRecipeOutput(table, $) {
    const output = table.find('.result, .crafting-recipe-result').first();
    const link = output.find('a').first();
    const quantityText = output.text();
    
    const quantity = quantityText.match(/×(\d+)/) ? parseInt(quantityText.match(/×(\d+)/)[1]) : 1;
    
    return {
      item: link.attr('title') || link.text().trim(),
      quantity: quantity
    };
  }

  extractSmeltingMaterials(table, $) {
    const materials = {};
    
    const input = table.find('.input, .smelting-recipe-input').first();
    const link = input.find('a').first();
    
    if (link.length > 0) {
      const material = link.attr('title') || link.text().trim();
      materials[material] = 1;
    }
    
    return materials;
  }

  getFallbackResourceData(resourceName) {
    // Return basic fallback data when scraping fails
    return {
      name: resourceName,
      type: 'unknown',
      rarity: 'common',
      stackSize: 64,
      gatheringMethods: [{ method: 'mining', efficiency: 'medium', requirements: ['pickaxe'] }],
      locations: ['overworld'],
      tools: ['pickaxe'],
      uses: [],
      lastUpdated: Date.now(),
      isFallback: true
    };
  }

  getFallbackRecipe(itemName) {
    // Return null for unknown recipes
    return null;
  }

  // Utility methods
  async searchItems(query) {
    const searchUrl = `${this.baseUrl}Special:Search?search=${encodeURIComponent(query)}&go=Go`;
    
    try {
      const response = await axios.get(searchUrl, {
        timeout: 5000,
        headers: {
          'User-Agent': 'AppyProx/1.0.0 (Minecraft Automation Bot)'
        }
      });

      const $ = cheerio.load(response.data);
      const results = [];
      
      $('.mw-search-results li').each((i, result) => {
        const title = $(result).find('.mw-search-result-heading a').text().trim();
        const snippet = $(result).find('.searchresult').text().trim();
        
        if (title) {
          results.push({ title, snippet });
        }
      });
      
      return results;
    } catch (error) {
      this.logger.error(`Search failed for query "${query}":`, error.message);
      return [];
    }
  }

  clearCache() {
    this.resourceCache.clear();
    this.recipeCache.clear();
    this.itemCache.clear();
    
    // Delete cache files
    try {
      const cacheFiles = ['resources.json', 'recipes.json', 'items.json'];
      for (const file of cacheFiles) {
        const filePath = path.join(this.cacheDir, file);
        if (fs.existsSync(filePath)) {
          fs.unlinkSync(filePath);
        }
      }
      this.logger.info('Wiki cache cleared');
    } catch (error) {
      this.logger.error('Failed to clear wiki cache:', error.message);
    }
  }

  getStatus() {
    return {
      initialized: this.initialized,
      cacheStats: {
        resources: this.resourceCache.size,
        recipes: this.recipeCache.size,
        items: this.itemCache.size
      },
      cacheDuration: this.cacheDuration
    };
  }
}

module.exports = WikiScraper;