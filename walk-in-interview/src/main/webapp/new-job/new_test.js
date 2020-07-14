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
  'page-title': 'New Job Post',
  'cancel': 'Cancel',
  'submit': 'Create',
  'error-message': 'There is an error in the following field: ',
  'title': 'Job Title',
  'description': 'Job Description',
  'address': 'Job Address',
  'postal-code': 'Postal Code',
  'requirements-title': 'Requirements',
  'requirements-list': 'requirements-list',
  'pay-title': 'Job Pay',
  'pay-frequency': {
    'HOURLY': 'Hourly',
    'WEEKLY': 'Weekly',
    'MONTHLY': 'Monthly',
    'YEARLY': 'Yearly',
  },
  'pay-min': 'min (sgd)',
  'pay-max': 'max (sgd)',
  'duration-title': 'Job Duration',
  'duration': {
    'ONE_WEEK': '1 Week',
    'TWO_WEEKS': '2 Weeks',
    'ONE_MONTH': '1 Month',
    'SIX_MONTHS': '6 Months',
    'ONE_YEAR': '1 Year',
    'OTHER': 'Other',
  },
  'expiry-title': 'Job Expiry',
};
  /**
   * TODO(issue/38): import getRequirementsList() here.
   */
const REQUIREMENTS_LIST = {
  'O_LEVEL': 'O Level',
  'LANGUAGE_ENGLISH': 'English',
  'DRIVING_LICENSE_C': 'Category C Driving License',
};
  /**
   * Note that if https is used instead in the url below then
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
const TIMEOUT = 20000;

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
    return driver.quit();
  });

  describe('Job Duration', () => {
    const titleId = 'duration-title';
    const durationId = 'duration';

    it('checks the title text', () => {
      return driver.findElement(By.id(titleId)).getText()
          .then((text) => {
            assert.equal(text, STRINGS[titleId]);
          });
    });

    it('checks correct select options rendered', () => {
      return driver.findElement(By.id(durationId)).getText()
          .then((text) => {
            const options = STRINGS[durationId];
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
