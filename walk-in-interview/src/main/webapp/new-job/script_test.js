/**
 * This file is where we will write tests for the
 * funcions in new-job/script.js using mocha and selenium.
 * Running `npm test` in the command line will run all
 * the mocha tests in the walk-in-interview directory.
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

    describe('Error Message', () => {
      const id = 'new-job-error-message';

      it('checks initially empty', () => {
        return driver.findElement(By.id(id)).getText()
            .then((text) => {
              assert.equal('', text);
            });
      });
    });

    describe('Job Title', () => {
      const id = 'new-job-title';

      it('checks the placeholder attribute', () => {
        return driver.findElement(By.id(id)).getAttribute('placeholder')
            .then((value) => {
              assert.equal(STRINGS[id], value);
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id(id)).getAttribute('type')
            .then((value) => {
              assert.equal('text', value);
            });
      });
    });

    describe('Job Description', () => {
      const id = 'new-job-description';

      it('checks the placeholder attribute', () => {
        return driver.findElement(By.id(id)).getAttribute('placeholder')
            .then((value) => {
              assert.equal(STRINGS[id], value);
            });
      });
    });

    describe('Job Address', () => {
      const id = 'new-job-address';

      it('checks the placeholder attribute', () => {
        return driver.findElement(By.id(id)).getAttribute('placeholder')
            .then((value) => {
              assert.equal(STRINGS[id], value);
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id(id)).getAttribute('type')
            .then((value) => {
              assert.equal('text', value);
            });
      });
    });

    describe('Job Requirements', () => {
      const titleId = 'new-job-requirements-title';
      const listId = 'new-job-requirements-list';

      it('checks the title text', () => {
        return driver.findElement(By.id(titleId)).getText()
            .then((text) => {
              assert.equal(STRINGS[id], text);
            });
      });

      // TODO(issue/xx): add tests to check if correct list rendered
      // (use the function)
    });

    describe('Job Pay', () => {
      const titleId = 'new-job-pay-title';
      const frequencyId = 'new-job-pay-frequency';
      const minId = 'new-job-pay-min';
      const maxId = 'new-job-pay-max';

      it('checks the title text', () => {
        return driver.findElement(By.id(titleId)).getText()
            .then((text) => {
              assert.equal(STRINGS[id], text);
            });
      });

      // TODO(issue/xx): write tests for the select options

      it('min: checks the placeholder attribute', () => {
        return driver.findElement(By.id(minId)).getAttribute('placeholder')
            .then((value) => {
              assert.equal(STRINGS[id], value);
            });
      });

      it('min: checks the type attribute', () => {
        return driver.findElement(By.id(minId)).getAttribute('type')
            .then((value) => {
              assert.equal('number', value);
            });
      });

      it('max: checks the placeholder attribute', () => {
        return driver.findElement(By.id(maxId)).getAttribute('placeholder')
            .then((value) => {
              assert.equal(STRINGS[id], value);
            });
      });

      it('max: checks the type attribute', () => {
        return driver.findElement(By.id(maxId)).getAttribute('type')
            .then((value) => {
              assert.equal('number', value);
            });
      });
    });

    describe('Job Duration', () => {
      const titleId = 'new-job-duration-title';
      const durationId = 'new-job-duration';

      it('checks the title text', () => {
        return driver.findElement(By.id(titleId)).getText()
            .then((text) => {
              assert.equal(STRINGS[id], text);
            });
      });

      // TODO(issue/xx): write tests for the select options
    });

    describe('Job Expiry', () => {
      const titleId = 'new-job-expiry-title';
      const expiryId = 'new-job-expiry';

      it('checks the title text', () => {
        return driver.findElement(By.id(titleId)).getText()
            .then((text) => {
              assert.equal(STRINGS[id], text);
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id(expiryId)).getAttribute('type')
            .then((value) => {
              assert.equal('date', value);
            });
      });

      it('checks the min attribute', () => {
        return driver.findElement(By.id(expiryId)).getAttribute('min')
            .then((value) => {
              // TODO(issue/xx): should equal to todays date
            });
      });

      it('checks the max attribute', () => {
        return driver.findElement(By.id(expiryId)).getAttribute('max')
            .then((value) => {
              // TODO(issue/xx): should equal to today + one years date
            });
      });
    });
  });
});
