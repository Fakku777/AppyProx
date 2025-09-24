#!/usr/bin/env node

/**
 * Test script for AI-powered task system
 * Verifies that the TaskAI integration is working properly
 */

const path = require('path');

// Set up paths
const srcPath = path.join(__dirname, 'src');

// Import the AppyProx Logger and TaskAI modules
const Logger = require(path.join(srcPath, 'proxy', 'utils', 'Logger'));
const TaskAI = require(path.join(srcPath, 'ai', 'TaskAI'));

// Set up logger using AppyProx Logger
const logger = new Logger({ level: 'info' });

/**
 * Test different natural language inputs
 */
const testCases = [
  {
    name: 'Simple Diamond Collection',
    input: 'collect 64 diamonds',
    options: { currentLocation: 'overworld' }
  },
  {
    name: 'Complex Boss Fight',
    input: 'defeat the ender dragon with diamond armor',
    options: { 
      currentLocation: 'overworld',
      inventory: ['diamond', 'stick'],
      groupSize: 4
    }
  },
  {
    name: 'Multi-step Task',
    input: 'go to 100 64 -200 and collect iron ore then craft iron armor',
    options: { 
      currentLocation: { x: 0, y: 70, z: 0 },
      groupSize: 2
    }
  },
  {
    name: 'Dragon Breath Collection',
    input: 'collect 5 dragon breath with glass bottles',
    options: {
      currentLocation: 'overworld',
      inventory: ['glass_bottle'],
      groupSize: 3
    }
  },
  {
    name: 'Crafting Task',
    input: 'craft 10 diamond swords with sharpness 5',
    options: {
      currentLocation: 'overworld',
      inventory: ['diamond', 'stick', 'enchanted_book']
    }
  },
  {
    name: 'Placement Task',
    input: 'place torches at 50 70 100',
    options: {
      currentLocation: { x: 45, y: 70, z: 95 }
    }
  },
  {
    name: 'Equipment Requirement',
    input: 'defeat ender dragon with full diamond armor protection 4',
    options: {
      currentLocation: 'overworld',
      groupSize: 4
    }
  }
];

/**
 * Run individual test case
 */
async function runTestCase(testCase, taskAI) {
  console.log(`\n🧪 Testing: ${testCase.name}`);
  console.log(`📝 Input: "${testCase.input}"`);
  console.log(`⚙️  Options:`, JSON.stringify(testCase.options, null, 2));
  
  try {
    const result = await taskAI.convertToTasks(testCase.input, testCase.options);
    
    if (result.success) {
      console.log(`✅ SUCCESS - Generated ${result.tasks.length} tasks`);
      console.log(`⏱️  Estimated time: ${Math.round(result.estimatedTime / 1000)}s`);
      console.log(`👥 Recommended group size: ${result.groupRecommendation}`);
      console.log(`📋 Tasks:`);
      
      result.tasks.forEach((task, index) => {
        console.log(`   ${index + 1}. ${task.type} - ${task.action}`);
        if (task.parameters.target) console.log(`      Target: ${task.parameters.target}`);
        if (task.parameters.item) console.log(`      Item: ${task.parameters.item}`);
        if (task.parameters.quantity) console.log(`      Quantity: ${task.parameters.quantity}`);
        if (task.parameters.coordinates) console.log(`      Coordinates: ${JSON.stringify(task.parameters.coordinates)}`);
        if (task.estimatedTime) console.log(`      Estimated time: ${Math.round(task.estimatedTime / 1000)}s`);
      });
      
      if (Object.keys(result.resourceRequirements).length > 0) {
        console.log(`🎯 Resource requirements:`, result.resourceRequirements);
      }
      
      return { success: true, taskCount: result.tasks.length };
    } else {
      console.log(`❌ FAILED - ${result.error}`);
      if (result.suggestions) {
        console.log(`💡 Suggestions:`);
        result.suggestions.forEach(suggestion => {
          console.log(`   • ${suggestion}`);
        });
      }
      return { success: false, error: result.error };
    }
  } catch (error) {
    console.log(`💥 ERROR - ${error.message}`);
    return { success: false, error: error.message };
  }
}

/**
 * Run all tests
 */
async function runAllTests() {
  console.log('🤖 AppyProx AI-Powered Task System Tests');
  console.log('========================================\n');
  
  // Initialize TaskAI
  const taskAI = new TaskAI(logger);
  
  let totalTests = 0;
  let passedTests = 0;
  let totalTasks = 0;
  
  // Run each test case
  for (const testCase of testCases) {
    const result = await runTestCase(testCase, taskAI);
    totalTests++;
    
    if (result.success) {
      passedTests++;
      totalTasks += result.taskCount || 0;
    }
    
    // Add delay between tests
    await new Promise(resolve => setTimeout(resolve, 500));
  }
  
  // Test Summary
  console.log('\n📊 Test Summary');
  console.log('===============');
  console.log(`Total tests: ${totalTests}`);
  console.log(`Passed: ${passedTests}`);
  console.log(`Failed: ${totalTests - passedTests}`);
  console.log(`Success rate: ${Math.round((passedTests / totalTests) * 100)}%`);
  console.log(`Total tasks generated: ${totalTasks}`);
  
  if (passedTests === totalTests) {
    console.log('\n🎉 All tests passed! AI task system is working correctly.');
  } else {
    console.log('\n⚠️  Some tests failed. Check the output above for details.');
  }
  
  // Test additional features
  console.log('\n🔍 Testing Additional Features:');
  console.log('==============================');
  
  // Test invalid input
  console.log('\n🧪 Testing invalid input...');
  try {
    const invalidResult = await taskAI.convertToTasks('', {});
    console.log('❌ Should have failed for empty input');
  } catch (error) {
    console.log('✅ Correctly rejected empty input');
  }
  
  // Test with minimal context
  console.log('\n🧪 Testing minimal context...');
  const minimalResult = await taskAI.convertToTasks('collect wood', {});
  if (minimalResult.success) {
    console.log('✅ Handled minimal context correctly');
  } else {
    console.log('❌ Failed to handle minimal context');
  }
  
  console.log('\n✅ Testing complete!');
}

/**
 * Test knowledge base coverage
 */
async function testKnowledgeBase() {
  console.log('\n📚 Testing Knowledge Base Coverage');
  console.log('==================================');
  
  const taskAI = new TaskAI(logger);
  
  // Test knowledge of different items
  const knowledgeTests = [
    'diamond_sword',
    'ender_dragon', 
    'dragon_breath',
    'protection',
    'end'
  ];
  
  console.log('\n🔍 Checking knowledge base entries...');
  
  knowledgeTests.forEach(item => {
    const hasItem = taskAI.minecraftKnowledge.items[item] !== undefined;
    const hasBoss = taskAI.minecraftKnowledge.bosses[item] !== undefined;
    const hasLocation = taskAI.minecraftKnowledge.locations[item] !== undefined;
    const hasEnchantment = taskAI.minecraftKnowledge.enchantments[item] !== undefined;
    
    if (hasItem || hasBoss || hasLocation || hasEnchantment) {
      console.log(`✅ ${item} - Found in knowledge base`);
    } else {
      console.log(`❌ ${item} - Not found in knowledge base`);
    }
  });
  
  // Check task pattern coverage
  console.log('\n🎯 Checking task pattern coverage...');
  const patterns = taskAI.taskPatterns;
  console.log(`Total patterns: ${patterns.length}`);
  patterns.forEach((pattern, index) => {
    console.log(`   ${index + 1}. ${pattern.type} - ${pattern.handler}`);
  });
}

// Run tests if this file is executed directly
if (require.main === module) {
  runAllTests()
    .then(() => testKnowledgeBase())
    .catch(error => {
      console.error('Test execution failed:', error);
      process.exit(1);
    });
}

module.exports = {
  runAllTests,
  testKnowledgeBase,
  runTestCase,
  testCases
};