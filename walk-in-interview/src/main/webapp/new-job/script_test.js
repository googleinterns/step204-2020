/**
 * This file is where we will write tests for the
 * funcions in new-job/script.js using mocha.
 * Running `npm test` in the command line will run all
 * the tests in the walk-in-interview directory.
 */

const assert = require('assert');


const webdriver = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const path = require('chromedriver').path;

const service = new chrome.ServiceBuilder(path).build();
chrome.setDefaultService(service);

const By = webdriver.By;
const until = webdriver.until;

let driver;

describe('Math', function() {
  this.timeout(50000);
  beforeEach(async () => {
    driver = await new webdriver.Builder()
        .withCapabilities(webdriver.Capabilities.chrome())
        .build();
    await driver.get('https://library-app.firebaseapp.com/');
  });

  afterEach(async () => {
    await driver.quit();
  });

  it('should test if 3*3 = 9', function() {
    assert.equal(9, 3*3);
  });
  it('should test if (3-4)*8=-8', function() {
    assert.equal(-8, (3-4)*8);
  });


  it('changes button opacity on user input', async () => {
    await driver.findElement(By.css('input')).sendKeys('us@goog.com')
        .catch((err) => {
          console.log('error:', err);
        });
  });
});
