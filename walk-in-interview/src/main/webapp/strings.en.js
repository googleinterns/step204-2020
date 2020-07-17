/**
 * This contains all the constants (language: English) for
 * the homepage.js file.
 */

/**
 * AppStrings will be set to {} if it is
 * falsy (false, 0, '', null, undefined, NaN)
 */
export const AppStrings = {};

/**
 * Common strings used in all pages.
 */
AppStrings['common'] = {
  'error-message': 'There is an error in the following field: ',
};

/**
 * All the strings to be used in the homepage folder.
 */
AppStrings['homepage'] = {
  'page-title': 'Find Walk-in Interviews',
  'new-post': 'New Post',
  'account': 'Create an Account',
  'sort-by-title': 'Sort By',
  'sort-by': {
    'DISTANCE': 'Distance',
    'SALARY': 'Salary',
  },
  'sort-by-order': {
    'ASCENDING': 'Ascending',
    'DESCENDING': 'Descending',
  },
  'sort-by-submit': 'Apply',
  'filter-by-title': 'Filter By',
  'filter-distance-title': 'Distance',
  'filter-distance-min': 'min (km)',
  'filter-distance-max': 'max (km)',
  'filter-salary-title': 'Salary',
  'filter-salary-min': 'min (sgd)',
  'filter-salary-max': 'max (sgd)',
  'filter-by-submit': 'Apply Filters',
  'job-listings-title': 'Job Listings:',
};

/**
 * All the strings to be used in the new-job folder.
 */
AppStrings['new-job'] = {
  'page-title': 'New Job Post',
  'cancel': 'Cancel',
  'submit': 'Create',
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
