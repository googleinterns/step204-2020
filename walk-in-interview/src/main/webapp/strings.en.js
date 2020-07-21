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
 * Error messages used in all pages.
 */
AppStrings['error-message'] = {
  'field': 'There is an error in the following field: ',
  'creating-job': 'There was an error while creating ' +
      'the job listing, please try submitting again',
  'no-jobs': 'There are no jobs to display at the moment.',
  'getting-jobs': 'An error occured while getting the job listings.',
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
    'SALARY-DESCENDING': 'Salary (high to low)',
    'SALARY-ASCENDING': 'Salary (low to high)',
  },
  'show-by-region-title': 'Show By Region',
  'show-by-region': {
    'ENTIRE': 'Entire Singapore',
    'CENTRAL': 'Central Singapore',
    'WEST': 'West Singapore',
    'EAST': 'East Singapore',
    'NORTH': 'North Singapore',
    'NORTH_EAST': 'North-East Singapore',
  },
  'filter-limits-title': 'Add Limits (optional)',
  'filter-min-limit': 'min (annual pay for salary)',
  'filter-max-limit': 'max (annual pay for salary)',
  'filters-submit': 'Apply Filters',
  'job-listings-title': 'Job Listings:',
  'job-listings-showing': 'out of',
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
