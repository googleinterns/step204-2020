/**
 * This file is specific to index.html for homepage. It renders the fields
 * on the page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dnamic imports
 */
import {AppStrings} from './strings.en.js';

const STRINGS = AppStrings['homepage'];
const JOBPAGE_PATH = '../new-job/index.html';

// TODO(issue/34): implement pagination for job listings
const DEFAULT_PAGE_SIZE = 20;
const DEFAULT_PAGE_INDEX = 0;

window.onload = () => {
  addHomepageElements();
};

/** Adds all the titles to the fields on this page. */
function addHomepageElements() {
  const homepageTitle = document.getElementById('homepage-page-title');
  homepageTitle.innerText = STRINGS['homepage-page-title'];

  const newPostButton = document.getElementById('homepage-new-post');
  newPostButton.innerText = STRINGS['homepage-new-post'];
  newPostButton.addEventListener('click', (_) => {
    window.location.href= JOBPAGE_PATH;
  });

  const accountButton = document.getElementById('homepage-account');
  accountButton.innerText = STRINGS['homepage-account'];

  const sortByTitle = document.getElementById('homepage-sort-by-title');
  sortByTitle.innerText = STRINGS['homepage-sort-by-title'];
  addJobSortOptions();
  addJobOrderOptions();
  addJobSortSubmit();

  const filterByTitle = document.getElementById('homepage-filter-by-title');
  filterByTitle.innerText = STRINGS['homepage-filter-by-title'];

  const distanceFilterTitle =
    document.getElementById('homepage-filter-distance-title');
  distanceFilterTitle.innerText = STRINGS['homepage-filter-distance-title'];

  const distanceMin = document.getElementById('homepage-filter-distance-min');
  distanceMin.setAttribute('type', 'number');
  distanceMin.setAttribute('placeholder',
      STRINGS['homepage-filter-distance-min']);

  const distanceMax = document.getElementById('homepage-filter-distance-max');
  distanceMax.setAttribute('type', 'number');
  distanceMax.setAttribute('placeholder',
      STRINGS['homepage-filter-distance-max']);

  const salaryFilterTitle =
    document.getElementById('homepage-filter-salary-title');
  salaryFilterTitle.innerText = STRINGS['homepage-filter-salary-title'];

  const salaryMin = document.getElementById('homepage-filter-salary-min');
  salaryMin.setAttribute('type', 'number');
  salaryMin.setAttribute('placeholder',
      STRINGS['homepage-filter-salary-min']);

  const salaryMax = document.getElementById('homepage-filter-salary-max');
  salaryMax.setAttribute('type', 'number');
  salaryMax.setAttribute('placeholder',
      STRINGS['homepage-filter-salary-max']);
  addJobFilterSubmit();

  const jobListingsTitle =
    document.getElementById('homepage-job-listings-title');
  jobListingsTitle.innerText = STRINGS['homepage-job-listings-title'];

  const defaultSortBy = document.getElementById('homepage-sort-by').value;
  const defaultSortOrder =
    document.getElementById('homepage-sort-by-order').value;
  getJobListings(defaultSortBy, defaultSortOrder, DEFAULT_PAGE_SIZE,
      DEFAULT_PAGE_INDEX);
}

/** Dynamically add the options for sorting the jobs. */
function addJobSortOptions() {
  const jobSortSelect = document.getElementById('homepage-sort-by');

  addSelectOptions(jobSortSelect, STRINGS['homepage-sort-by']);
}

/** Dynaimcally add the options for orders of the sorting options. */
function addJobOrderOptions() {
  const jobOrderSelect = document.getElementById('homepage-sort-by-order');

  addSelectOptions(jobOrderSelect, STRINGS['homepage-sort-by-order']);
}

/**
 * Add the keys and values from the options map to the select element.
 * @param {Element} select The select element.
 * @param {Map} options The map of options to be added.
 */
function addSelectOptions(select, options) {
  select.options.length = 0;

  for (const key in options) {
    if (options.hasOwnProperty(key)) {
      select.options[select.options.length] =
        new Option(options[key], key);
    }
  }
}

/**
 * Add the attributes and on click function to the sorting
 * submit button.
 */
function addJobSortSubmit() {
  const sortBySubmit = document.getElementById('homepage-sort-by-submit');
  sortBySubmit.setAttribute('type', 'submit');
  sortBySubmit.setAttribute('value', STRINGS['homepage-sort-by-submit']);

  sortBySubmit.addEventListener('click', (_) => {
    if (!validSortByInput()) {
      return;
    }

    const sortByParam = document.getElementById('homepage-sort-by').value;
    const sortOrderParam = document.getElementById('homepage-sort-by-order');
    getJobListings(sortByParam, sortOrderParam, DEFAULT_PAGE_SIZE,
        DEFAULT_PAGE_INDEX);
  });
}

/**
 * Add the attributes and on click function to the filters
 * submit button.
 */
function addJobFilterSubmit() {
  const filterBySubmit = document.getElementById('homepage-filter-by-submit');
  filterBySubmit.setAttribute('type', 'submit');
  filterBySubmit.setAttribute('value', STRINGS['homepage-filter-by-submit']);

  /**
   * TODO(issue/35): add filtering event listener
   * + validation + GET request functions
   */
}

/**
 * Makes GET request to retrieve all the job listings from the database
 * given the sorting and order. This function is called when the
 * homepage is loaded and also when the sorting is changed.
 * @param {String} sortedBy How the jobs should be sorted.
 * @param {String} order The order of the sorting.
 * @param {int} pageSize The number of jobs for the page.
 * @param {int} pageIndex The page index (starting from 0).
 */
function getJobListings(sortedBy, order, pageSize, pageIndex) {
  // TODO(issue/18): get jobs from database and render them on screen
}
