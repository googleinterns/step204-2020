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
  'log-in': 'Log In',
  'log-out': 'Log Out',
  'show-job-posts-made': 'Show Job Posts Made',
  'interested-job-list': 'Interested Jobs',
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
  'details': 'Show Details',
  'job-listings-showing': 'out of',
  'no-jobs-error-message': 'There are no jobs to display at the moment.',
  'get-jobs-error-message': 'An error occurred while getting the job listings.',
  'job-details-error-message': 'An error occurred while getting ' +
    'the details of this job.',
};

/**
 * All the strings to be used in the business-jobs-list/index.html.
 */
AppStrings['business-jobs-list'] = {
  'back': 'Back',
  'job-listings-title': 'Job Posts Made:',
  'no-jobs-error-message': 'There are no jobs to display at the moment.',
  'get-jobs-error-message': 'An error occurred while getting the job listings.',
};

/**
 * All the strings to be used in the applicant-interested-list/index.html.
 */
AppStrings['applicant-interested-list'] = {
  'back': 'Back',
  'job-listings-title': 'My Interested Job Listings:',
  'no-jobs-error-message': 'There are no jobs to display at the moment.',
  'get-jobs-error-message': 'An error occurred while getting the job listings.',
};

/**
 * All the strings to be used in the account/create-account/index.html.
 */
AppStrings['create-account'] = {
  'create-applicant-account': 'Create an Applicant account',
  'create-business-account': 'Create a Business account',
  'back': 'Back',
  'submit': 'Submit',
  'error-message': 'Error occur. Please try again.',
  'create-account-error-message': 'Some error occur when creating the account.',
};

/**
 * All the strings to be used in the account/create-account/applicant/index.html.
 */
AppStrings['create-applicant-account'] = {
  'name': 'Name',
  'skills-title': 'Skills',
  'new-user-info': 'Account is successfully created!',
};

/**
 * All the strings to be used in the account/create-account/business
 */
AppStrings['create-business-account'] = {
  'account': 'Account (Email)',
  'password': 'Password',
  'empty-email': 'Empty email input',
  'invalid-password': 'The password must have at least 6 characters',
  'new-user-info': 'Account is successfully created!',
  'name': 'Company Name',
  'user-in-use-error': 'There already exists an account with the given email address',
  'invalid-email-error': 'The email address is not valid',
  'operation-not-allowed-error': 'If email/password accounts are not enabled',
  'weak-password-error': 'The password is not strong enough\nWe need a password with at least 6 characters',
};

/**
 * All the strings to be used in the account/log-in/index.html.
 */
AppStrings['log-in'] = {
  'applicant-log-in': 'Log in as an Applicant',
  'business-log-in': 'Log in as a Business',
  'back': 'Back',
  'submit': 'Submit',
  'error-message': 'Error occur. Please try again.',
};


/**
 * All the strings to be used in the account/log-in/applicant/index.html.
 */
AppStrings['applicant-log-in'] = {
  'new-user-info': 'This phone number is new to our system. An account is created automatically',
};

/**
 * All the strings to be used in the account/log-in/business/index.html.
 */
AppStrings['business-log-in'] = {
  'account': 'Account (Email)',
  'password': 'Password',
  'empty-email': 'Empty email input',
  'invalid-email-error': 'The email address is not valid',
  'user-disabled': 'The user corresponding to the given email has been ' +
    'disabled by some administrator or Access Management to prevent ' +
    'further logins to that account',
  'user-not-found': 'There is no user corresponding to the given email',
  'wrong-password': 'The password is invalid for the given email',
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
  'pay-title': 'Job Pay',
  'pay-frequency': {
    'HOURLY': 'Hourly',
    'WEEKLY': 'Weekly',
    'MONTHLY': 'Monthly',
    'YEARLY': 'Yearly',
  },
  'pay-min': 'min (sgd)',
  'pay-max': 'max (sgd)',
  'sgd': 'SGD',
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
  'creating-job-error-message': 'There was an error while creating ' +
      'the job listing, please try submitting again',

  'jobAddressDescription': '{ADDRESS}, {POSTAL_CODE}',
  'jobPayDescription': '{MIN_PAY} - {MAX_PAY} {CURRENCY} ({FREQUENCY})',
  'requirementsDescription': 'Requirements List: {REQUIREMENTS_LIST}',
  'jobShowing': '{MINIMUM} - {MAXIMUM} out of {TOTAL_COUNT}',
};

/**
 * Strings to be used in the new-job folder.
 */
AppStrings['new-job'] = {
  'page-title': 'New Job Post',
  'submit': 'Create',
  'error-message': 'There was an error while creating ' +
    'the job listing, please try submitting again',
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
};

/**
 * Strings used in job-details.
 */
AppStrings['job-details'] = {
  'back-to-homepage': 'Back To Homepage',
  'update': 'Update',
  'delete': 'Delete',
  'error-message': 'There was an error while getting ' +
    'the job post. Please try again',
  'error-message':
    'There was an error while deleting the job post. Please try again',
  'update-error-message': 'Error occur when directing to update page',
  'delete-error-message': 'There was an error while deleting ' +
    'the job post. Please try again',
};

/**
 * Strings used in firebase-auth.
 */
AppStrings['auth'] = {
  'sign-in-failure': 'Error occur when signing in',
  'sign-in-success': 'Signed In Successfully',
  'sign-out-success': 'Signed Out Successfully',
  'sign-out-failure': 'Error occur when signing out',
};
