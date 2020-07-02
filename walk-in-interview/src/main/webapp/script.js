
/**
 * Ways in which the user can sort the job listings.
 * @enum {Stirng}
 */
const SORT_BY = {
  DISTANCE: 'Distance', // default
  SALARY: 'Salary',
};

/**
 * Ways in which the user can order the job listings.
 * @enum {Stirng}
 */
const ORDER_BY = {
  ASCENDING: 'Ascending', // default
  DESCENDING: 'Descending',
};

const SORT_BY_SELECT_ID = 'sort-by-select';
const ORDER_BY_SELECT_ID = 'order-by-select';
const DEFAULT_PAGE_SIZE = 20;
const DEFAULT_PAGE_INDEX = 0;

/** Called when homepage is loaded. */
function loadHomepage() {
  addJobSortOptions();
  addJobOrderOptions();
  getJobListings(SORT_BY.DISTANCE, ORDER_BY.ASCENDING,
      DEFAULT_PAGE_SIZE, DEFAULT_PAGE_INDEX);
}

/** Dynamically add the options for sorting the jobs. */
function addJobSortOptions() {
  const jobSortSelect = document.getElementById(SORT_BY_SELECT_ID);
  jobSortSelect.options.length = 0;

  addSelectOptions(jobSortSelect, SORT_BY);
}

/** Dynaimcally add the options for orders of the sorting options. */
function addJobOrderOptions() {
  const jobOrderSelect = document.getElementById(ORDER_BY_SELECT_ID);
  jobOrderSelect.options.length = 0;

  addSelectOptions(jobOrderSelect, ORDER_BY);
}

/**
 * Add the keys and values from the options map to the select element.
 * @param {Element} select The select element.
 * @param {Map} options The map of options to be added.
 */
function addSelectOptions(select, options) {
  for (key in options) {
    if ({}.hasOwnProperty.call(options, key)) {
      select.options[select.options.length] =
        new Option(options[key], key);
    }
  }
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
  // TODO(issue/18): get jobs from database
}
