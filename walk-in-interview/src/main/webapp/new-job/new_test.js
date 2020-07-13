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
 * Unable to import AppStrings into this file so copy/
 * pasted here instead.
 * TODO(issue/38): import AppStrings here
 */
const STRINGS = {
  'new-job-page-title': 'New Job Post',
  'new-job-cancel': 'Cancel',
  'new-job-submit': 'Create',
  'new-job-error-message': 'There is an error in the following field: ',
  'new-job-title': 'Job Title',
  'new-job-description': 'Job Description',
  'new-job-address': 'Job Address',
  'new-job-postal-code': 'Postal Code',
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
  /**
   * Note that if https is used instead in the url below then
   * we get the following error: ERR_SSL_PROTOCOL_ERROR
   */
const JOBPAGE_URL = 'http://localhost:3000/new-job/index.html';
const HOMEPAGE_URL = 'http://localhost:3000/index.html';

const assert = require('chai').assert;
const webdriver = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const path = require('chromedriver').path;

const service = new chrome.ServiceBuilder(path).build();
chrome.setDefaultService(service);

const By = webdriver.By;
const until = webdriver.until;
const TIMEOUT = 10000;

let driver;

/** Note that this.timeout() will not work arrow functions. */
describe('New Job Tests', function() {
  this.timeout(TIMEOUT);

  beforeEach(() => {
    driver = new webdriver.Builder()
        .withCapabilities(webdriver.Capabilities.chrome())
        .build();
    return driver.get(JOBPAGE_URL);
  });

  afterEach(() => {
    // return driver.quit();
  });
  const STRINGS = {
    'new-job-pay-frequency': {
      'HOURLY': 'Hourly',
      'WEEKLY': 'Weekly',
      'MONTHLY': 'Monthly',
      'YEARLY': 'Yearlyo',
    },
  };

  describe('Error Message', () => {
    const id = 'new-job-pay-frequency';

    it('checks select yeee', () => {
      return driver.findElement(By.id(id)).getText()
          .then((text) => {
            const options = STRINGS['new-job-pay-frequency'];
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
});
