/**
 * This file is where we will write tests for the
 * funcions in new-job/script.js using mocha and selenium.
 * Running `npm test` in the command line will run all
 * the mocha tests in the walk-in-interview directory.
 * Also note that the Dev App Server should be running
 * using `mvn package appengine:run` before running the tests.
 * To get test report formatted nicely, cd into this folder and
 * run `mocha script_test.js --reporter mochawesome --reporter-options
 * autoOpen=true`.
 */

/**
 * Unable to make imports from other files.
 *  TODO(issue/38): import getRequirementsList() here.
 */
const REQUIREMENTS_LIST = {
  'O_LEVEL': 'O Level',
  'LANGUAGE_ENGLISH': 'English',
  'DRIVING_LICENSE_C': 'Category C Driving License',
};
/**
 * Note that if `https` is used instead in the url below then
 * we get the following error: ERR_SSL_PROTOCOL_ERROR
 */
const JOBPAGE_URL = 'http://localhost:3000/new-job/index.html';
const HOMEPAGE_URL = 'http://localhost:3000/homepage/index.html';

const assert = require('chai').assert;
const webdriver = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const path = require('chromedriver').path;

const service = new chrome.ServiceBuilder(path).build();
chrome.setDefaultService(service);

const By = webdriver.By;
const until = webdriver.until;
const options = new chrome.Options();
options.addArguments('headless');

const TIMEOUT = 50000;

let driver;

/** Note that this.timeout() will not work with arrow functions. */
describe('New Job Tests', function() {
  this.timeout(TIMEOUT);

  beforeEach(() => {
    driver = new webdriver.Builder().setChromeOptions(options)
        .withCapabilities(webdriver.Capabilities.chrome())
        .build();
    return driver.get(JOBPAGE_URL);
  });

  afterEach(() => {
    return driver.quit();
  });

  describe('Page Rendering Tests', () => {
    describe('Page Title', () => {
      it('checks the title text', () => {
        return driver.findElement(By.id('page-title')).getText()
            .then((title) => {
              assert.equal(title, 'New Job Post');
            });
      });
    });

    describe('Cancel Button', () => {
      it('checks the value attribute', () => {
        return driver.findElement(By.id('cancel')).getAttribute('value')
            .then((value) => {
              assert.equal(value, 'Cancel');
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id('cancel')).getAttribute('type')
            .then((value) => {
              assert.equal(value, 'reset');
            });
      });
    });

    describe('Submit Button', () => {
      it('checks the value attribute', () => {
        return driver.findElement(By.id('submit')).getAttribute('value')
            .then((value) => {
              assert.equal(value, 'Create');
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id('submit')).getAttribute('type')
            .then((value) => {
              assert.equal(value, 'submit');
            });
      });
    });

    describe('Error Message', () => {
      it('checks initially empty', () => {
        return driver.findElement(By.id('error-message')).getText()
            .then((msg) => {
              assert.isEmpty(msg);
            });
      });
    });

    describe('Job Title', () => {
      it('checks the placeholder attribute', () => {
        return driver.findElement(By.id('title')).getAttribute('placeholder')
            .then((value) => {
              assert.equal(value, 'Job Title');
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id('title')).getAttribute('type')
            .then((value) => {
              assert.equal(value, 'text');
            });
      });
    });

    describe('Job Description', () => {
      it('checks the placeholder attribute', () => {
        return driver.findElement(By.id('description'))
            .getAttribute('placeholder')
            .then((value) => {
              assert.equal(value, 'Job Description');
            });
      });
    });

    describe('Job Address', () => {
      it('checks the placeholder attribute', () => {
        return driver.findElement(By.id('address'))
            .getAttribute('placeholder')
            .then((value) => {
              assert.equal(value, 'Job Address');
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id('address'))
            .getAttribute('type')
            .then((value) => {
              assert.equal(value, 'text');
            });
      });

      describe('Postal Code', () => {
        it('checks the placeholder attribute', () => {
          return driver.findElement(By.id('postal-code'))
              .getAttribute('placeholder')
              .then((value) => {
                assert.equal(value, 'Postal Code');
              });
        });

        it('checks the type attribute', () => {
          return driver.findElement(By.id('postal-code'))
              .getAttribute('type')
              .then((value) => {
                assert.equal(value, 'text');
              });
        });
      });
    });

    describe('Job Requirements', () => {
      it('checks the title text', () => {
        return driver.findElement(By.id('requirements-title'))
            .getText()
            .then((title) => {
              assert.equal(title, 'Requirements');
            });
      });

      it('checks correct requirement list rendered', () => {
        return driver.findElement(By.id('requirements-list'))
            .getText()
            .then((text) => {
              const list = REQUIREMENTS_LIST;
              for (const key in list) {
                if (list.hasOwnProperty(key)) {
                  if (!text.includes(list[key])) {
                    assert.fail(list[key] + ' requirement not included');
                  }
                }
              }
            });
      });
    });

    describe('Job Pay', () => {
      it('checks the title text', () => {
        return driver.findElement(By.id('pay-title')).getText()
            .then((title) => {
              assert.equal(title, 'Job Pay');
            });
      });

      it('checks correct select options rendered', () => {
        return driver.findElement(By.id('pay-frequency')).getText()
            .then((text) => {
              const options = {
                'HOURLY': 'Hourly',
                'WEEKLY': 'Weekly',
                'MONTHLY': 'Monthly',
                'YEARLY': 'Yearly',
              };
              for (const key in options) {
                if (options.hasOwnProperty(key)) {
                  if (!text.includes(options[key])) {
                    assert.fail(options[key] + ' option not included');
                  }
                }
              }
            });
      });

      it('min: checks the placeholder attribute', () => {
        return driver.findElement(By.id('pay-min')).getAttribute('placeholder')
            .then((value) => {
              assert.equal(value, 'min (sgd)');
            });
      });

      it('min: checks the type attribute', () => {
        return driver.findElement(By.id('pay-min')).getAttribute('type')
            .then((value) => {
              assert.equal(value, 'number');
            });
      });

      it('max: checks the placeholder attribute', () => {
        return driver.findElement(By.id('pay-max'))
            .getAttribute('placeholder')
            .then((value) => {
              assert.equal(value, 'max (sgd)');
            });
      });

      it('max: checks the type attribute', () => {
        return driver.findElement(By.id('pay-max'))
            .getAttribute('type')
            .then((value) => {
              assert.equal(value, 'number');
            });
      });
    });

    describe('Job Duration', () => {
      it('checks the title text', () => {
        return driver.findElement(By.id('duration-title')).getText()
            .then((title) => {
              assert.equal(title, 'Job Duration');
            });
      });

      it('checks correct select options rendered', () => {
        return driver.findElement(By.id('duration')).getText()
            .then((text) => {
              const options = {
                'ONE_WEEK': '1 Week',
                'TWO_WEEKS': '2 Weeks',
                'ONE_MONTH': '1 Month',
                'SIX_MONTHS': '6 Months',
                'ONE_YEAR': '1 Year',
                'OTHER': 'Other',
              };
              for (const key in options) {
                if (options.hasOwnProperty(key)) {
                  if (!text.includes(options[key])) {
                    assert.fail(options[key] + ' option not included');
                  }
                }
              }
            });
      });
    });

    describe('Job Expiry', () => {
      it('checks the title text', () => {
        return driver.findElement(By.id('expiry-title')).getText()
            .then((title) => {
              assert.equal(title, 'Job Expiry');
            });
      });

      it('checks the type attribute', () => {
        return driver.findElement(By.id('expiry')).getAttribute('type')
            .then((value) => {
              assert.equal(value, 'date');
            });
      });

      it('checks the min attribute', () => {
        return driver.findElement(By.id('expiry')).getAttribute('min')
            .then((value) => {
              const expected = new Date().toISOString().substr(0, 10);

              assert.equal(value, expected);
            });
      });

      it('checks the max attribute', () => {
        return driver.findElement(By.id('expiry')).getAttribute('max')
            .then((value) => {
              const date = new Date();
              date.setFullYear(date.getFullYear() + 1);
              const expected = date.toISOString().substr(0, 10);

              assert.equal(value, expected);
            });
      });
    });
  });

  describe('Page Functionality Tests', () => {
    describe('Cancel Button', () => {
      /**
       * Clicking the cancel button should return the user to the homepage
       * and not make any POST request.
       */
      it('should return to homepage', () => {
        return driver.findElement(By.id('cancel')).click()
            .then(() => driver.wait(until.urlIs(HOMEPAGE_URL)))
            .then(() => driver.getCurrentUrl())
            .then((currUrl) => assert.equal(currUrl, HOMEPAGE_URL));
      });
    });

    /**
     * If a field is not valid, then clicking submit will display an error
     * message with the invalid field, and no POST request will be made.
     */
    describe('Submit Button', () => {
      const date = new Date();
      const today = `${(date.getMonth() + 1)}-${date.getDate()}` +
        `-${date.getFullYear()}`;

      beforeEach('add all valid inputs', () => {
        /**
         * This will add all valid values to the job creation fields.
         * The fields that need to be tested can be cleared in the
         * individual test accordingly.
         */
        return driver.findElement(By.id('title')).sendKeys('Waiter')
            .then(() => driver.findElement(By.id('description'))
                .sendKeys('wait on tables'))
            .then(() => driver.findElement(By.id('address'))
                .sendKeys('290 Orchard Rd'))
            .then(() => driver.findElement(By.id('postal-code'))
                .sendKeys('238859'))
            .then(() => driver.findElement(By.id('pay-frequency'))
                .sendKeys('HOURLY'))
            .then(() => driver.findElement(By.id('pay-min'))
                .sendKeys('5'))
            .then(() => driver.findElement(By.id('pay-max'))
                .sendKeys('6'))
            .then(() => driver.findElement(By.id('duration'))
                .sendKeys('OTHER'))
            .then(() => driver.findElement(By.id('expiry'))
                .sendKeys(today));
      });

      it('no job title', () => {
        /**
         * The job title must be cleared here to test it.
         */
        return driver.findElement(By.id('title')).clear()
            .then(() => driver.findElement(By.id('submit')).click())
            .then(() => driver.findElement(By.id('error-message')).getText())
            .then((text) => assert.equal(text,
                'There is an error in the following field: Job Title'));
      });

      it('should not be false postive', () => {
        /**
         * The job title must be cleared here to test it.
         */
        return driver.findElement(By.id('title')).clear()
            .then(() => driver.findElement(By.id('submit')).click())
            .then(() => driver.findElement(By.id('error-message')).getText())
            .then((text) => assert.notEqual(text,
                'There is an error in the following field: '));
      });

      it('incorrect job address format', () => {
        // TODO(issue/13&33): add tests for address once places api implemented
      });

      it('incorrect postal code', () => {
        // TODO(issue/13&33): add tests for address once places api implemented
      });

      it('min greater than max', () => {
        /**
         * The pay-min and pay-max fields must be cleared before sending
         * another key.
         */
        return driver.findElement(By.id('pay-min')).clear()
            .then(() => driver.findElement(By.id('pay-max')).clear())
            .then(() => driver.findElement(By.id('pay-min')).sendKeys('7'))
            .then(() => driver.findElement(By.id('pay-max')).sendKeys('6'))
            .then(() => driver.findElement(By.id('submit')).click())
            .then(() => driver.findElement(By.id('error-message')).getText())
            .then((text) => assert.equal(text,
                'There is an error in the following field: Job Pay'));
      });

      it('job duration not chosen', () => {
        /**
         * Note that the .clear() function does not work on
         * non-input/textarea elements. Also .sendKeys() cannot
         * be used twice on the same element.
         */
        driver.get(JOBPAGE_URL);

        /**
         * Job duration not set below since we are testing it.
         */
        return driver.findElement(By.id('title')).sendKeys('Waiter')
            .then(() => driver.findElement(By.id('description'))
                .sendKeys('wait on tables'))
            .then(() => driver.findElement(By.id('address'))
                .sendKeys('290 Orchard Rd'))
            .then(() => driver.findElement(By.id('postal-code'))
                .sendKeys('238859'))
            .then(() => driver.findElement(By.id('pay-frequency'))
                .sendKeys('HOURLY'))
            .then(() => driver.findElement(By.id('pay-min'))
                .sendKeys('5'))
            .then(() => driver.findElement(By.id('pay-max'))
                .sendKeys('6'))
            .then(() => driver.findElement(By.id('expiry'))
                .sendKeys(today))
            .then(() => driver.findElement(By.id('submit')).click())
            .then(() => driver.findElement(By.id('error-message')).getText())
            .then((text) => assert.equal(text,
                'There is an error in the following field: Job Duration'));
      });

      it('expiry date not chosen', () => {
        /**
         * The job expiry must be cleared here to test it.
         */
        return driver.findElement(By.id('expiry')).clear()
            .then(() => driver.findElement(By.id('submit')).click())
            .then(() => driver.findElement(By.id('error-message')).getText())
            .then((text) => assert.equal(text,
                'There is an error in the following field: Job Expiry'));
      });

      /**
       * If all the fields are valid, then a POST request should be made and the
       * user should be returned to the homepage.
       */
      it('should return to homepage', () => {
        return driver.findElement(By.id('submit')).click()
            .then(() => driver.wait(until.urlIs(HOMEPAGE_URL)))
            .then(() => driver.getCurrentUrl())
            .then((currUrl) => assert.equal(currUrl, HOMEPAGE_URL));
      });

      /**
       * TODO(issue/40): check that POST request has been made
       * & also check that the response is what was expected
       */
    });
  });
});
