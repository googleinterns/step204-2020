/**
 * This contains all the constants (language: English).
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
  'empty-job-id-error-message': 'Empty Job Id found.',
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
 * All the common strings to be used in the job information related page.
 */
AppStrings['job'] = {
  'cancel': 'Cancel',
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
 * Strings to be used in the new-job folder.
 */
AppStrings['new-job'] = {
  'page-title': 'New Job Post',
  'submit': 'Create',
  'error-message': 
    'There was an error while creating the job listing, please try submitting again',
};

/**
 * Strings to be used in the update-job folder.
 */
AppStrings['update-job'] = {
  'page-title': 'Update Job Post',
  'update': 'Update',
  'error-message': 
    'There was an error while loading the job post. Please try again',
  'storing-error-message': 
    'There was an error while storing the job post. Please try again',
}

/*
 * Strings used in delete-job.
 */
AppStrings['delete-job'] = {
  'delete': 'Delete',
  'error-message': 
    'There was an error while deleting the job post. Please try again',
};

/*
 * Strings used in job-details.
 */
AppStrings['job-details'] = {
  'update-error-message': 'Error occur when directing to update page',
};
