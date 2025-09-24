/**
 * AI-Powered Task System Examples for AppyProx
 * 
 * These examples demonstrate how to use the natural language task conversion system
 * to create sophisticated automation tasks through simple descriptions.
 */

const axios = require('axios');

const BASE_URL = 'http://localhost:3000'; // AppyProx API base URL

/**
 * Example 1: Simple Resource Gathering
 */
async function simpleGatheringExample() {
  console.log('=== Example 1: Simple Resource Gathering ===');
  
  try {
    const response = await axios.post(`${BASE_URL}/tasks/from-language`, {
      description: "collect 64 diamonds",
      options: {
        currentLocation: 'overworld',
        groupSize: 2
      },
      cluster: 'mining_team'
    });
    
    console.log('Task created:', response.data);
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

/**
 * Example 2: Complex Boss Fight Task
 */
async function complexBossFightExample() {
  console.log('\n=== Example 2: Complex Boss Fight Task ===');
  
  try {
    const response = await axios.post(`${BASE_URL}/tasks/from-language`, {
      description: "defeat the ender dragon with diamond armor and collect dragon breath",
      options: {
        currentLocation: 'overworld',
        inventory: ['diamond', 'stick', 'glass_bottle'],
        groupSize: 4
      },
      cluster: 'dragon_slayers'
    });
    
    console.log('Boss fight task created:', response.data);
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

/**
 * Example 3: Building Task with Location
 */
async function buildingTaskExample() {
  console.log('\n=== Example 3: Building Task ===');
  
  try {
    const response = await axios.post(`${BASE_URL}/tasks/from-language`, {
      description: "craft 5 diamond swords and place them at 100 64 200",
      options: {
        currentLocation: { x: 50, y: 70, z: 150 },
        groupSize: 1
      },
      cluster: 'builders'
    });
    
    console.log('Building task created:', response.data);
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

/**
 * Example 4: Just Parse Natural Language (Don't Create Task)
 */
async function parseOnlyExample() {
  console.log('\n=== Example 4: Parse Natural Language Only ===');
  
  try {
    const response = await axios.post(`${BASE_URL}/tasks/parse-language`, {
      description: "defeat the ender dragon with full diamond armor protection 4",
      options: {
        currentLocation: 'overworld',
        groupSize: 4
      }
    });
    
    console.log('Parsing result:', JSON.stringify(response.data, null, 2));
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

/**
 * Example 5: Advanced Multi-step Task
 */
async function advancedMultiStepExample() {
  console.log('\n=== Example 5: Advanced Multi-step Task ===');
  
  try {
    const response = await axios.post(`${BASE_URL}/tasks/from-language`, {
      description: "go to -200 11 300 and collect 128 iron ore then craft full iron armor with protection 4",
      options: {
        currentLocation: { x: 0, y: 64, z: 0 },
        inventory: ['pickaxe', 'food', 'torches'],
        groupSize: 3
      },
      cluster: 'iron_workers'
    });
    
    console.log('Multi-step task created:', response.data);
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

/**
 * Example 6: Dragon Breath Collection (Special Item)
 */
async function specialItemExample() {
  console.log('\n=== Example 6: Dragon Breath Collection ===');
  
  try {
    const response = await axios.post(`${BASE_URL}/tasks/from-language`, {
      description: "collect 10 dragon breath during ender dragon fight",
      options: {
        currentLocation: 'overworld',
        inventory: ['glass_bottle', 'diamond_sword', 'diamond_armor'],
        groupSize: 4
      },
      cluster: 'dragon_team'
    });
    
    console.log('Dragon breath task created:', response.data);
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

// API Usage Examples for different scenarios
const exampleUsages = [
  {
    name: "Resource Collection",
    examples: [
      "collect 64 diamonds",
      "gather 256 stone blocks", 
      "collect 32 dragon eggs",
      "gather 1000 wood logs"
    ]
  },
  {
    name: "Combat Tasks",
    examples: [
      "defeat the ender dragon",
      "defeat the ender dragon with full diamond armor",
      "kill 50 zombies with sharpness 5 sword"
    ]
  },
  {
    name: "Movement & Location",
    examples: [
      "go to 100 64 -200",
      "travel to the nether",
      "place torch at 50 70 100"
    ]
  },
  {
    name: "Crafting Tasks", 
    examples: [
      "craft 10 diamond swords",
      "craft full diamond armor set",
      "craft enchanted book protection 4"
    ]
  },
  {
    name: "Complex Multi-step",
    examples: [
      "collect 64 diamonds then craft diamond armor with protection 4",
      "defeat the ender dragon and collect dragon breath with glass bottles",
      "go to -100 11 200 and collect iron ore then return to base"
    ]
  }
];

/**
 * Run all examples
 */
async function runAllExamples() {
  console.log('🤖 AppyProx AI-Powered Task System Examples\n');
  console.log('These examples show how to convert natural language into structured Minecraft tasks.\n');
  
  // Display usage examples
  console.log('📝 Natural Language Task Examples:');
  exampleUsages.forEach(category => {
    console.log(`\n${category.name}:`);
    category.examples.forEach(example => {
      console.log(`  • "${example}"`);
    });
  });
  
  console.log('\n🚀 Running Live Examples with API...\n');
  
  // Run live examples (uncomment to test with running AppyProx instance)
  /*
  await simpleGatheringExample();
  await complexBossFightExample(); 
  await buildingTaskExample();
  await parseOnlyExample();
  await advancedMultiStepExample();
  await specialItemExample();
  */
  
  console.log('\n✅ Examples completed! Uncomment the live API calls to test with a running AppyProx instance.');
}

// Run examples if this file is executed directly
if (require.main === module) {
  runAllExamples().catch(console.error);
}

module.exports = {
  simpleGatheringExample,
  complexBossFightExample,
  buildingTaskExample,
  parseOnlyExample,
  advancedMultiStepExample,
  specialItemExample,
  exampleUsages
};