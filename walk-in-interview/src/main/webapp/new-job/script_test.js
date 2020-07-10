/**
 * This file is where we will write tests for the
 * funcions in new-job/script.js using mocha and selenium.
 * Running `npm test` in the command line will run all
 * the mocha tests in the walk-in-interview directory.
 */

/**
 * Unable to import AppStrings into this file so copy/
 * pasted here instead.
 */
STRINGS = {
  'new-job-page-title': 'New Job Post',
  'new-job-cancel': 'Cancel',
  'new-job-submit': 'Create',
  'new-job-error-message': 'There is an error in the following field: ',
  'new-job-title': 'Job Title',
  'new-job-description': 'Job Description',
  'new-job-address': 'Job Address',
  'new-job-requirements-title': 'Requirements',
  'new-job-requirements-list': 'requirements-list',
  'new-job-pay-title': 'Job Pay',
  'new-job-pay-frequency': {
    'HOURLY': 'Hourly',
    'WEEKLY': 'Weekly',
    'MONTHLY': 'Monthly',
    'YEARLY': 'Yearly',
  },
  'new-job-pay-min': 'min (sgd)',
  'new-job-pay-max': 'max (sgd)',
  'new-job-duration-title': 'Job Duration',
  'new-job-duration': {
    'ONE_WEEK': '1 Week',
    'TWO_WEEKS': '2 Weeks',
    'ONE_MONTH': '1 Month',
    'SIX_MONTHS': '6 Months',
    'ONE_YEAR': '1 Year',
    'OTHER': 'Other',
  },
  'new-job-expiry-title': 'Job Expiry',
};

const assert = require('assert');
const webdriver = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const path = require('chromedriver').path;

const service = new chrome.ServiceBuilder(path).build();
chrome.setDefaultService(service);

const By = webdriver.By;
const until = webdriver.until;

let driver;

/** Note that this.timeout() will not work arrow functions. */
describe('New Job Tests', function() {
  this.timeout(50000);

  beforeEach(() => {
    driver = new webdriver.Builder()
        .withCapabilities(webdriver.Capabilities.chrome())
        .build();
    // TODO(issue/31): figure out how to test our local host url
    return driver.get('https://riyanar-step-2020.appspot.com/comments.html');
    // return driver.get('https://library-app.firebaseapp.com/');
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

      // TODO(issue/32): check correct list elements have been rendered
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

      // TODO(issue/32): check correct select options have been rendered

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

      // TODO(issue/32): check correct select options have been rendered
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
              const expected = new Date().toISOString().substr(0, 10);

              assert.equal(expected, value);
            });
      });

      it('checks the max attribute', () => {
        return driver.findElement(By.id(expiryId)).getAttribute('max')
            .then((value) => {
              const date = new Date();
              date.setFullYear(date.getFullYear() + 1);
              const expected = date.toISOString().substr(0, 10);

              assert.equal(expected, value);
            });
      });
    });
  });

  // TODO(issue/33): add test for page functionality
});
