/**
 * This file is specific to index.html for homepage. It renders the fields
 * on the page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from './strings.en.js';

const STRINGS = AppStrings['homepage'];
const JOBPAGE_PATH = '/new-job/index.html';
const RESPONSE_ERROR = 'An error occured while getting the job listings.';
const NO_JOBS_ERROR = 'There are no jobs to display at the moment.';

const SALARY_PARAM = 'SALARY';

// TODO(issue/34): implement pagination for job listings
const DEFAULT_PAGE_SIZE = 20;
const DEFAULT_PAGE_INDEX = 0;

window.onload = () => {
  renderHomepageElements();
};

/** Adds all the titles to the fields on this page. */
function renderHomepageElements() {
  const homepageTitle = document.getElementById('page-title');
  homepageTitle.innerText = STRINGS['page-title'];

  const newPostButton = document.getElementById('new-post');
  newPostButton.innerText = STRINGS['new-post'];
  newPostButton.addEventListener('click', (_) => {
    window.location.href= JOBPAGE_PATH;
  });

  const accountButton = document.getElementById('account');
  accountButton.innerText = STRINGS['account'];

  const sortByTitle = document.getElementById('sort-by-title');
  sortByTitle.innerText = STRINGS['sort-by-title'];

  renderSelectOptions(/** for */ 'sort-by');

  const showByTitle = document.getElementById('show-by-region-title');
  showByTitle.innerText = STRINGS['show-by-region-title'];

  renderSelectOptions(/** for */ 'show-by-region');

  const LimitsTitle = document.getElementById('filter-limits-title');
  LimitsTitle.innerText = STRINGS['filter-limits-title'];

  const minLimit = document.getElementById('filter-min-limit');
  minLimit.setAttribute('type', 'number');
  minLimit.setAttribute('placeholder', STRINGS['filter-min-limit']);

  const maxLimit = document.getElementById('filter-max-limit');
  maxLimit.setAttribute('type', 'number');
  maxLimit.setAttribute('placeholder', STRINGS['filter-max-limit']);

  renderJobFiltersSubmit();

  const jobListingsTitle =
    document.getElementById('job-listings-title');
  jobListingsTitle.innerText = STRINGS['job-listings-title'];

  renderJobListings();

  /** reset the error to make sure no error msg initially present */
  setErrorMessage(/* msg */ '', /** includes default msg */ false);
}

/**
 * Sets the error message according to the param.
 * @param {String} msg the message that the error div should display.
 * @param {boolean} includesDefault whether the default
 * message should be included.
 */
function setErrorMessage(msg, includesDefault) {
  document.getElementById('error-message').innerText =
    (includesDefault ? STRINGS['error-message'] + msg : msg);
}

/**
 * Add the keys and values from the options map to the select element.
 * @param {String} id The select element id.
 */
function renderSelectOptions(id) {
  const select = document.getElementById(id);
  const options = STRINGS[id];

  select.options.length = 0;

  for (const key in options) {
    if (options.hasOwnProperty(key)) {
      select.options[select.options.length] =
        new Option(options[key], key);
    }
  }
}

/**
 * Checks that the filter fields are valid.
 * Note that the lower and upper limits can be left empty, by default
 * the lower limit is 0 and the upper limit is Integer.MAX_VALUE
 * @return {boolean} Indication of whether the fields are valid.
 */
function validateFilters() {
  const sortByParam = document.getElementById('sort-by').value;
  const showByParam = document.getElementById('show-by-region').value;
  const minLimitParam = document.getElementById('filter-min-limit')
      .valueAsNumber;
  const maxLimitParam = document.getElementById('filter-max-limit')
      .valueAsNumber;

  if (showByParam == '' || sortByParam == '') {
    /** no need to show error message as this would not be the user's fault */
    console.log('error', 'region or sorting was empty');
    return false;
  }

  /** If the field has been filled out, we check that it's a positive int */
  if (!Number.isNaN(minLimitParam) &&
    (!Number.isInteger(minLimitParam) || minLimitParam < 0)) {
    setErrorMessage(/* msg */ STRINGS['filter-min-limit'],
        /** includes default msg */ true);
    return false;
  }

  /** If the field has been filled out, we check that it's a positive int */
  if (!Number.isNaN(maxLimitParam) &&
  (!Number.isInteger(maxLimitParam) || maxLimitParam < 0)) {
    setErrorMessage(/* msg */ STRINGS['filter-max-limit'],
        /** includes default msg */ true);
    return false;
  }

  /** If both fields have been filled out, we check that max >= min */
  if (!Number.isNaN(maxLimitParam) && !Number.isNaN(minLimitParam) &&
    maxLimitParam < minLimitParam) {
    setErrorMessage(/* msg */ STRINGS['filter-max-limit'],
        /** includes default msg */ true);
    return false;
  }

  return true;
}

/**
 * Add the attributes and on click function to the filters
 * submit button.
 */
function renderJobFiltersSubmit() {
  const filtersSubmit = document.getElementById('filters-submit');
  filtersSubmit.setAttribute('type', 'submit');
  filtersSubmit.setAttribute('value', STRINGS['filters-submit']);

  filtersSubmit.addEventListener('click', (_) => {
    renderJobListings();
  });
}

/**
 * This will add all the job listings onto the homepage.
 * @param {Object} jobPageData The details to be shown on the homepage.
 */
function displayJobListings(jobPageData) {
  const jobListingsElement = document.getElementById('job-listings');
  const jobShowing = document.getElementById('job-listings-showing');

  /** reset the list so we don't render the same jobs twice */
  jobListingsElement.innerHTML = '';
  jobShowing.innerHTML = '';

  if (jobPageData === undefined ||
    !jobPageData.hasOwnProperty('jobList') ||
    jobPageData['jobList'].length === 0) {
    setErrorMessage(/* msg */ NO_JOBS_ERROR,
        /** includes default msg */ false);
    return;
  }

  const jobListings = jobPageData['jobList'];

  const jobListingTemplate = document.getElementById('job-listing-template');

  jobListings.forEach((job) => {
    const jobListing =
      jobListingTemplate.cloneNode( /** and child elements */ true);
    jobListing.setAttribute('id', job['jobId']);

    const jobTitle = jobListing.children[0];
    jobTitle.innerText = job['jobTitle'];

    const jobAddress = jobListing.children[1];
    const location = job['jobLocation'];
    jobAddress.innerText = `${location['address']}, ${location['postalCode']}`;

    const jobPay = jobListing.children[2];
    const pay = job['jobPay'];
    jobPay.innerText = `${pay['min']} - ${pay['max']} SGD ` +
      `(${pay['paymentFrequency'].toLowerCase()})`;

    const requirementsList = jobListing.children[3];
    // TODO(issue/xx): temporary
    requirementsList.innerText = 'Requirements List: ' + job['requirements'];

    jobListingsElement.appendChild(jobListing);
  });

  jobShowing.innerText = `${jobPageData['range'].minimum} -` +
    ` ${jobPageData['range'].maximum} ${STRINGS['job-listings-showing']} ` +
    `${jobPageData['totalCount']}`;
}

/**
 * Add the list of jobs that are stored in the database
 * given the filter fields.
 */
async function renderJobListings() {
  if (!validateFilters()) {
    return;
  }

  const sortingParam = document.getElementById('sort-by').value;

  /** Note that this platform currently only sorts by Salary.*/
  if (!sortingParam.includes(SALARY_PARAM)) {
    console.log('error', 'this app currently only sorts by salary');
    return;
  }

  const sortByParam = SALARY_PARAM;
  const orderByParam = sortingParam.substring(7);

  const regionParam = document.getElementById('show-by-region').value;
  let minLimitParam = document.getElementById('filter-min-limit')
      .valueAsNumber;
  let maxLimitParam = document.getElementById('filter-max-limit')
      .valueAsNumber;

  if (Number.isNaN(minLimitParam)) {
    minLimitParam = 0;
  }

  if (Number.isNaN(maxLimitParam)) {
    maxLimitParam = Number.MAX_SAFE_INTEGER;
  }

  const jobPageData = await getJobListings(regionParam, sortByParam,
      minLimitParam, maxLimitParam, orderByParam,
      DEFAULT_PAGE_SIZE, DEFAULT_PAGE_INDEX)
      .catch((error) => {
        console.log('error', error);
        setErrorMessage(/* msg */ RESPONSE_ERROR,
            /** include default msg */ false);
      });

  displayJobListings(jobPageData);
}

/**
 * Makes GET request to retrieve all the job listings from the database
 * given the sorting and order. This function is called when the
 * homepage is loaded and also when the sorting is changed.
 * @param {String} region The Singapore region.
 * @param {String} sortBy How the jobs should be sorted.
 * @param {int} minLimit The lower limit for filtering.
 * @param {int} maxLimit The upper limit for filtering.
 * @param {String} order The order of the sorting.
 * @param {int} pageSize The number of jobs for one page.
 * @param {int} pageIndex The page index (starting from 0).
 * @return {Object} The data returned from the servlet.
 */
function getJobListings(region, sortBy, minLimit, maxLimit,
    order, pageSize, pageIndex) {
  const params = `region=${region}&sortBy=${sortBy}&minLimit=${minLimit}&` +
    `maxLimit=${maxLimit}&order=${order}&pageSize=${pageSize}` +
    `&pageIndex=${pageIndex}`;

  return fetch(`/jobs/listings?${params}`)
      .then((response) => response.json())
      .then((data) => {
        console.log('data', data);
        /** reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* msg */ '', /** include default msg */ false);
        return data;
      });
}
