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
  renderJobSortSubmit();

  const showByTitle = document.getElementById('show-by-region-title');
  showByTitle.innerText = STRINGS['show-by-region-title'];

  renderSelectOptions(/** for */ 'show-by-region');
  renderShowRegionSubmit();

  const filterByTitle = document.getElementById('filter-by-title');
  filterByTitle.innerText = STRINGS['filter-by-title'];

  const distanceFilterTitle =
    document.getElementById('filter-distance-title');
  distanceFilterTitle.innerText = STRINGS['filter-distance-title'];

  const distanceMin = document.getElementById('filter-distance-min');
  distanceMin.setAttribute('type', 'number');
  distanceMin.setAttribute('placeholder',
      STRINGS['filter-distance-min']);

  const distanceMax = document.getElementById('filter-distance-max');
  distanceMax.setAttribute('type', 'number');
  distanceMax.setAttribute('placeholder',
      STRINGS['filter-distance-max']);

  const salaryFilterTitle =
    document.getElementById('filter-salary-title');
  salaryFilterTitle.innerText = STRINGS['filter-salary-title'];

  const salaryMin = document.getElementById('filter-salary-min');
  salaryMin.setAttribute('type', 'number');
  salaryMin.setAttribute('placeholder',
      STRINGS['filter-salary-min']);

  const salaryMax = document.getElementById('filter-salary-max');
  salaryMax.setAttribute('type', 'number');
  salaryMax.setAttribute('placeholder',
      STRINGS['filter-salary-max']);
  renderJobFilterSubmit();

  const jobListingsTitle =
    document.getElementById('job-listings-title');
  jobListingsTitle.innerText = STRINGS['job-listings-title'];

  const defaultSortBy = document.getElementById('sort-by').value;

  /** Note that this platform currently only sorts by Salary.*/
  if (defaultSortBy.includes('SALARY')) {
    const sortByParam = 'SALARY';
    const orderByParam = defaultSortBy.substring(7);
    renderJobListings(sortByParam, orderByParam, DEFAULT_PAGE_SIZE,
        DEFAULT_PAGE_INDEX);
  }

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
 * Checks that the sorting is valid.
 * @return {boolean} Indication of whether the fields are valid.
 */
function validSortByInput() {
  const sortByParam = document.getElementById('sort-by').value;

  if (sortByParam == '') {
    /** no need to show error message as this would not be the user's fault */
    console.log('error', 'sorting was empty');
    return false;
  }

  return true;
}

/**
 * Checks that the region and sorting are valid.
 * @return {boolean} Indication of whether the fields are valid.
 */
function validShowRegionInput() {
  const showByParam = document.getElementById('show-by-region').value;

  if (showByParam == '' || !validSortByInput()) {
    /** no need to show error message as this would not be the user's fault */
    console.log('error', 'region or sorting was empty');
    return false;
  }

  return true;
}

/**
 * Add the attributes and on click function to the sorting
 * submit button.
 */
function renderJobSortSubmit() {
  const sortBySubmit = document.getElementById('sort-by-submit');
  sortBySubmit.setAttribute('type', 'submit');
  sortBySubmit.setAttribute('value', STRINGS['sort-by-submit']);

  sortBySubmit.addEventListener('click', (_) => {
    if (!validSortByInput()) {
      return;
    }

    const sortingParam = document.getElementById('sort-by').value;

    /** Note that this platform currently only sorts by Salary.*/
    if (sortingParam.includes('SALARY')) {
      const sortByParam = 'SALARY';
      const orderByParam = sortingParam.substring(7);
      renderJobListings(sortByParam, orderByParam, DEFAULT_PAGE_SIZE,
          DEFAULT_PAGE_INDEX);
    }
  });
}

/**
 * Add the attributes and on click function to the region
 * submit button.
 */
function renderShowRegionSubmit() {
  const showBySubmit = document.getElementById('show-by-region-submit');
  showBySubmit.setAttribute('type', 'submit');
  showBySubmit.setAttribute('value', STRINGS['show-by-region-submit']);

  showBySubmit.addEventListener('click', (_) => {
    if (!validShowRegionInput()) {
      return;
    }

    const sortingParam = document.getElementById('sort-by').value;
    const regionParam = document.getElementById('show-by-region').value;

    /** Note that this platform currently only sorts by Salary.*/
    if (sortingParam.includes('SALARY')) {
      const sortByParam = 'SALARY';
      const orderByParam = sortingParam.substring(7);
      renderJobListingsByRegion(regionParam, sortByParam, orderByParam,
          DEFAULT_PAGE_SIZE, DEFAULT_PAGE_INDEX);
    }
  });
}

/**
 * Add the attributes and on click function to the filters
 * submit button.
 */
function renderJobFilterSubmit() {
  const filterBySubmit = document.getElementById('filter-by-submit');
  filterBySubmit.setAttribute('type', 'submit');
  filterBySubmit.setAttribute('value', STRINGS['filter-by-submit']);

  /**
   * TODO(issue/35): add filtering event listener
   * + validation + GET request functions
   */
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
 * Add the list of jobs that are stored in the database.
 * @param {String} sortBy How the jobs should be sorted.
 * @param {String} order The order of the sorting.
 * @param {int} pageSize The number of jobs for one page.
 * @param {int} pageIndex The page index (starting from 0).
 */
async function renderJobListings(sortBy, order, pageSize, pageIndex) {
  if (!validSortByInput()) {
    return;
  }

  const jobPageData = await getJobListings(sortBy, order, pageSize, pageIndex)
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
 * @param {String} sortBy How the jobs should be sorted.
 * @param {String} order The order of the sorting.
 * @param {int} pageSize The number of jobs for one page.
 * @param {int} pageIndex The page index (starting from 0).
 * @return {Object} The data returned from the servlet.
 */
function getJobListings(sortBy, order, pageSize, pageIndex) {
  const params = `sortBy=${sortBy}&order=${order}` +
    `&pageSize=${pageSize}&pageIndex=${pageIndex}`;

  return fetch(`/jobs?${params}`)
      .then((response) => response.json())
      .then((data) => {
        console.log('data', data);
        /** reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* msg */ '', /** include default msg */ false);
        return data;
      });
}

/**
 * Add the list of jobs that are stored in the database based on region.
 * @param {String} region The region of Singapore.
 * @param {String} sortBy How the jobs should be sorted.
 * @param {String} order The order of the sorting.
 * @param {int} pageSize The number of jobs for one page.
 * @param {int} pageIndex The page index (starting from 0).
 */
async function renderJobListingsByRegion(region, sortBy, order,
    pageSize, pageIndex) {
  if (!validShowRegionInput()) {
    return;
  }

  const jobPageData = await getJobListingsByRegion(region, sortBy, order,
      pageSize, pageIndex)
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
 * @param {String} region The region in Singapore.
 * @param {String} sortBy How the jobs should be sorted.
 * @param {String} order The order of the sorting.
 * @param {int} pageSize The number of jobs for one page.
 * @param {int} pageIndex The page index (starting from 0).
 * @return {Object} The data returned from the servlet.
 */
function getJobListingsByRegion(region, sortBy, order, pageSize, pageIndex) {
  const params = `region=${region}&sortBy=${sortBy}&order=${order}` +
    `&pageSize=${pageSize}&pageIndex=${pageIndex}`;

  return fetch(`/jobs/by-region?${params}`)
      .then((response) => response.json())
      .then((data) => {
        console.log('data', data);
        /** reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* msg */ '', /** include default msg */ false);
        return data;
      });
}
