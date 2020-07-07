/**
 * This file is where we will write tests for the
 * funcions in new-job/script.js using mocha.
 * Running `npm test` in the command line will run all
 * the tests in the walk-in-interview directory.
 */

const assert = require('assert');

// Create a test group named Math
// Create two tests within the group Math.
// Test one: Should test if 3*3 = 9
// Test two: Should test if (3–4)*8 = -8
describe('Math', function() {
  it('should test if 3*3 = 9', function() {
    assert.equal(3*3, 9);
  });
  it('should test if (3-4)*8=-8', function() {
    assert.equal((3-4)*8, -8);
  });
});
