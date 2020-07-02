
/**
 * Ways in which the user can sort the job listings.
 * @enum {stirng}
 */
const SORT_BY = {
  DISTANCE: 'Distance', // default
  SALARY: 'Salary',
};

/**
 * Ways in which the user can order the job listings.
 * @enum {stirng}
 */
const ORDER_BY = {
  ASCENDING: 'Ascending', //default
  DESCENDING: 'Descending',
};

const SORT_BY_SELECT_ID = 'sort-by-select';
const ORDER_BY_SELECT_ID = 'order-by-select';

/** Dynamically add the options for sorting the jobs. */
function addJobSortOptions() {
  const jobSortSelect = document.getElementById(SORT_BY_SELECT_ID);
  jobSortSelect.options.length = 0;

  // jobSortSelect.options[0] = new Option('Other', '');
  addSelectOptions(jobSortSelect, SORT_BY);
}

/** Dynaimcally add the options for ordering the jobs. */
function addJobOrderOptions() {
  const jobOrderSelect = document.getElementById(ORDER_BY_SELECT_ID);
  jobOrderSelect.options.length = 0;

  // jobOrderSelect.options[0] = new Option('Other', '');
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
