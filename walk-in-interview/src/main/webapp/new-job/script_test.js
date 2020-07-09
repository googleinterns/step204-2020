/**
 * This file is where we will write tests for the
 * funcions in new-job/script.js using mocha and selenium.
 * Running `npm test` in the command line will run all
 * the tests in the walk-in-interview directory.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dnamic imports
 */
import {AppStrings} from './strings.en.js';

const STRINGS = AppStrings['new-job'];

const assert = require('assert');
const webdriver = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const path = require('chromedriver').path;

const service = new chrome.ServiceBuilder(path).build();
chrome.setDefaultService(service);

const By = webdriver.By;
const until = webdriver.until;

let driver;

describe('New Job Tests', () => {
  this.timeout(50000);

  beforeEach(() => {
    driver = new webdriver.Builder()
        .withCapabilities(webdriver.Capabilities.chrome())
        .build();
    // TODO(issue/31): figure out how to test our local host url
    return driver.get('https://riyanar-step-2020.appspot.com/');
  });

  afterEach(() => {
    return driver.quit();
  });

  describe('Page Rendering Tests', () => {
    describe('Page Title', () => {
      const id = 'new-job-page-title';

      it('checks the title text', () => {
        return driver.findElement(By.id(id)).getText()
            .then((text) => {
              assert.equal(STRINGS[id], text);
            });
      });
    });

    describe('Cancel Button', () => {
      const id = 'new-job-cancel';

      it('checks the value attribute', () => {
        return driver.findElement(By.id(id)).getAttribute('value')
            .then((value) => {
              assert.equal(STRINGS[id], value);
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id(id)).getAttribute('type')
            .then((value) => {
              assert.equal('reset', value);
            });
      });
    });

    describe('Submit Button', () => {
      const id = 'new-job-submit';

      it('checks the value attribute', () => {
        return driver.findElement(By.id(id)).getAttribute('value')
            .then((value) => {
              assert.equal(STRINGS[id], value);
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id(id)).getAttribute('type')
            .then((value) => {
              assert.equal('submit', value);
            });
      });
    });
  });
});
