/**
 * This file is specific to new-job.html. It renders the fields on the
 * page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

/**
 * Titles for all the sections/fields in different languages.
 */
const NEW_JOB_PAGE_TITLE = {
  en: 'New Job Post',
};
const NEW_JOB_CANCEL_TITLE = {
  en: 'Cancel',
};
const NEW_JOB_SUBMIT_TITLE = {
  en: 'Create',
};
const NEW_JOB_TITLE = {
  en: 'Job Title',
};
const NEW_JOB_DESCRIPTION = {
  en: 'Describe the job here (responsibilities, preffered skills)...',
};
const NEW_JOB_ADDRESS = {
  en: 'Job Address',
};
const NEW_JOB_REQUIREMENTS_TITLE = {
  en: 'Requirements',
};
const NEW_JOB_PAY_TITLE = {
  en: 'Job Pay',
};
const NEW_JOB_PAY_MIN_TITLE = {
  en: 'min (sgd)',
};
const NEW_JOB_PAY_MAX_TITLE = {
  en: 'max (sgd)',
};
const NEW_JOB_DURATION_TITLE = {
  en: 'Job Duration',
};
const NEW_JOB_EXPIRY_TITLE = {
  en: 'Job Expiry',
};

const NEW_JOB_TITLE_ID = 'new-job-title';
const NEW_JOB_ADDRESS_ID = 'new-job-address';
const NEW_JOB_DESCRIPTION_ID = 'new-job-description';
const NEW_JOB_EXPIRY_ID = 'new-job-expiry';
const NEW_JOB_DURATION_ID = 'new-job-duration';
const REQUIREMENTS_LIST_NAME = 'requirements-list';
const NEW_JOB_REQUIREMENTS_LIST_ID = 'new-job-requirements-list';
const NEW_JOB_SUBMIT_ID = 'new-job-submit';
const NEW_JOB_CANCEL_ID = 'new-job-cancel';

const NEW_JOB_PAGE_TITLE_ID = 'new-job-page-title';
const NEW_JOB_REQUIREMENTS_TITLE_ID = 'new-job-requirements-title';
const NEW_JOB_PAY_TITLE_ID = 'new-job-pay-title';
const NEW_JOB_DURATION_TITLE_ID = 'new-job-duration-title';
const NEW_JOB_EXPIRY_TITLE_ID = 'new-job-expiry-title';

const HOMEPAGE_PATH = '/walk-in-interview/src/main/webapp/index.html';

/**
 * Fields related to job pay.
 */
const NEW_JOB_PAY = {
  FREQUENCY_ID: 'new-job-pay-frequency',
  MIN_ID: 'new-job-pay-min',
  MAX_ID: 'new-job-pay-max',
};

/**
 * Options for job duration.
 */
const JOB_DURATION = {
  ONE_WEEK: '1 Week',
  TWO_WEEKS: '2 Weeks',
  ONE_MONTH: '1 Month',
  SIX_MONTHS: '6 Months',
  ONE_YEAR: '1 Year',
};

/**
 * Options for frequency of job pay.
 */
const JOB_PAY_FREQUENCY = {
  HOURLY: 'Hourly',
  WEEKLY: 'Weekly',
  MONTHLY: 'Monthly',
  YEARLY: 'Yearly',
};

/**
 * Error messages.
 */
const NEW_JOB_ERROR_MESSAGE = {
  ID: 'new-job-error-message',
  INVALID_INPUT: 'Invalid inputs',
  ERROR_RESPONSE: 'Unable to create job listing',
};

window.onload = () => {
  addJobPageElements();
};

/** Adds all the titles to the fields on this page. */
function addJobPageElements() {
  const cancelButton = document.getElementById(NEW_JOB_CANCEL_ID);
  cancelButton.setAttribute('value', NEW_JOB_CANCEL_TITLE.en);
  cancelButton.setAttribute('type', 'reset');

  const jobPageTitle = document.getElementById(NEW_JOB_PAGE_TITLE_ID);
  jobPageTitle.innerText = NEW_JOB_PAGE_TITLE.en;

  const submitButton = document.getElementById(NEW_JOB_SUBMIT_ID);
  submitButton.setAttribute('value', NEW_JOB_SUBMIT_TITLE.en);
  submitButton.setAttribute('type', 'submit');

  const jobTitle = document.getElementById(NEW_JOB_TITLE_ID);
  jobTitle.setAttribute('type', 'text');
  jobTitle.setAttribute('placeholder', NEW_JOB_TITLE.en);
  jobTitle.setAttribute('required', true);

  const jobDescription = document.getElementById(NEW_JOB_DESCRIPTION_ID);
  jobDescription.setAttribute('placeholder', NEW_JOB_DESCRIPTION.en);
  jobDescription.setAttribute('required', true);

  const jobAddress = document.getElementById(NEW_JOB_ADDRESS_ID);
  jobAddress.setAttribute('placeholder', NEW_JOB_ADDRESS.en);
  jobAddress.setAttribute('required', true);

  const requirementsTitle =
    document.getElementById(NEW_JOB_REQUIREMENTS_TITLE_ID);
  requirementsTitle.innerText = NEW_JOB_REQUIREMENTS_TITLE.en;
  addRequirementsList();

  const payTitle = document.getElementById(NEW_JOB_PAY_TITLE_ID);
  payTitle.innerText = NEW_JOB_PAY_TITLE.en;
  addJobPayFrequencyOptions();

  const payMin = document.getElementById(NEW_JOB_PAY.MIN_ID);
  payMin.setAttribute('type', 'number');
  payMin.setAttribute('placeholder', NEW_JOB_PAY_MIN_TITLE.en);
  payMin.setAttribute('required', true);

  const payMax = document.getElementById(NEW_JOB_PAY.MAX_ID);
  payMax.setAttribute('type', 'number');
  payMax.setAttribute('placeholder', NEW_JOB_PAY_MAX_TITLE.en);
  payMax.setAttribute('required', true);

  const durationTitle = document.getElementById(NEW_JOB_DURATION_TITLE_ID);
  durationTitle.innerText = NEW_JOB_DURATION_TITLE.en;
  addJobDurationOptions();

  const expiryTitle = document.getElementById(NEW_JOB_EXPIRY_TITLE_ID);
  expiryTitle.innerText = NEW_JOB_EXPIRY_TITLE.en;
  const expiryInput = document.getElementById(NEW_JOB_EXPIRY_ID);
  expiryInput.setAttribute('type', 'date');
  expiryInput.setAttribute('name', NEW_JOB_EXPIRY_ID);
  expiryInput.setAttribute('required', true);
  addJobExpiryLimits();
}

/** Add the list of requirements that are stored in the database. */
function addRequirementsList() {
  const requirementsList = getRequirementsList();
  const requirementsListElement =
    document.getElementById(NEW_JOB_REQUIREMENTS_LIST_ID);

  requirementsListElement.innerHTML = '';
  for (key in requirementsList) {
    if ({}.hasOwnProperty.call(requirementsList, key)) {
      const requirementElement = document.createElement('li');
      requirementElement.setAttribute('id', key);

      const checkbox = document.createElement('input');
      checkbox.setAttribute('type', 'checkbox');
      checkbox.setAttribute('id', key);
      checkbox.setAttribute('name', REQUIREMENTS_LIST_NAME);
      checkbox.setAttribute('value', key);

      const label = document.createElement('label');
      label.setAttribute('for', key);
      label.innerText = requirementsList[key];

      requirementElement.appendChild(checkbox);
      requirementElement.appendChild(label);

      requirementsListElement.appendChild(requirementElement);
    }
  }
}

/**
 * Gets the requirements list from the servlet
 * (which gets it from the database).
 * @return {Object} the requirements list in a key-value mapping format.
 */
function getRequirementsList() {
  // TODO(issue/17): GET request to servlet to get from database
  // returning some hardcoded values for now
  return {
    'o-levels': 'O Levels',
    'drivers-license': 'Drivers License',
  };
}

/** Dynamically add the options for job pay frequency. */
function addJobPayFrequencyOptions() {
  const jobPaySelect = document.getElementById(NEW_JOB_PAY.FREQUENCY_ID);
  jobPaySelect.setAttribute('required', true);
  jobPaySelect.options.length = 0;

  jobPaySelect.options[0] = new Option('Select', '');
  jobPaySelect.options[0].setAttribute('disabled', true);
  addSelectOptions(jobPaySelect, JOB_PAY_FREQUENCY);
}

/** Dynamically add the options for job duration. */
function addJobDurationOptions() {
  const jobDurationSelect = document.getElementById(NEW_JOB_DURATION_ID);
  jobDurationSelect.options.length = 0;

  jobDurationSelect.options[0] = new Option('Other', '');
  addSelectOptions(jobDurationSelect, JOB_DURATION);
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

/** Dynamically add the limits for choosing the new job post expiry. */
function addJobExpiryLimits() {
  const date = new Date();
  const min = date.toISOString().substr(0, 10);
  date.setFullYear(date.getFullYear() + 1);
  const max = date.toISOString().substr(0, 10);

  const datePicker = document.getElementById(NEW_JOB_EXPIRY_ID);
  datePicker.setAttribute('min', min);
  datePicker.setAttribute('max', max);
}

/**
 * Gets job detail from user input.
 * @return {Object} containing the user inputs.
 */
function getJobDetailsFromUserInput() {
  const name = document.getElementById(NEW_JOB_TITLE_ID).value;
  const address = document.getElementById(NEW_JOB_ADDRESS_ID).value;
  const description = document.getElementById(NEW_JOB_DESCRIPTION_ID).value;
  const payFrequency = document.getElementById(NEW_JOB_PAY.FREQUENCY_ID).value;
  const payMin = document.getElementById(NEW_JOB_PAY.MIN_ID).valueAsNumber;
  const payMax = document.getElementById(NEW_JOB_PAY.MAX_ID).valueAsNumber;

  const requirementsCheckboxes =
    document.getElementsByName(REQUIREMENTS_LIST_NAME);
  const requirementsList = [];
  requirementsCheckboxes.forEach(({checked, id}) => {
    if (checked) {
      requirementsList.push(id);
    }
  });

  const expiry = document.getElementById(NEW_JOB_EXPIRY_ID).valueAsNumber;
  const duration = document.getElementById(NEW_JOB_DURATION_ID).value;

  const jobDetails = {
    jobTitle: name,
    jobLocation: {
      address: address,
      lat: 1.3039, // TODO(issue/13): get these from places api
      lon: 103.8358,
    },
    jobDescription: description,
    jobPay: {
      frequency: payFrequency,
      min: payMin,
      max: payMax,
    },
    requirements: requirementsList,
    postExpiry: expiry,
  };

  if (duration !== '') {
    jobDetails.jobDuration = duration;
  }

  return jobDetails;
}

/**
 * Validates the user input
 * @return {boolean} depending on whether the input is valid or not.
 */
function validateRequiredUserInput() {
  // TODO(issue/19): add more validation checks
  const name = document.getElementById(NEW_JOB_TITLE_ID).value;
  const address = document.getElementById(NEW_JOB_ADDRESS_ID).value;
  const description = document.getElementById(NEW_JOB_DESCRIPTION_ID).value;
  const payFrequency = document.getElementById(NEW_JOB_PAY.FREQUENCY_ID).value;
  const payMin = document.getElementById(NEW_JOB_PAY.MIN_ID).valueAsNumber;
  const payMax = document.getElementById(NEW_JOB_PAY.MAX_ID).valueAsNumber;
  const expiry = document.getElementById(NEW_JOB_EXPIRY_ID).valueAsNumber;

  if (payMin > payMax) {
    return false;
  }

  if (name !== '' && address !== '' && description !== '' &&
    payFrequency !== '' && payMin !== '' && payMax !== '' &&
    !Number.isNaN(expiry)) {
    return true;
  }

  return false;
}

const submitButton = document.getElementById(NEW_JOB_SUBMIT_ID);
submitButton.addEventListener('click', (_) => {
  if (!validateRequiredUserInput()) {
    document.getElementById(NEW_JOB_ERROR_MESSAGE.ID).innerText =
      NEW_JOB_ERROR_MESSAGE.INVALID_INPUT;
    return;
  }

  const jobDetails = getJobDetailsFromUserInput();
  fetch('/jobs', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(jobDetails),
  })
      .then((response) => response.text())
      .then((data) => {
        console.log('data', data);
        document.getElementById(NEW_JOB_ERROR_MESSAGE.ID).innerText = '';
        window.location.href= HOMEPAGE_PATH;
      })
      .catch((error) => {
        document.getElementById(NEW_JOB_ERROR_MESSAGE.ID).innerText =
          NEW_JOB_ERROR_MESSAGE.ERROR_RESPONSE;
        console.log('error', error);
      });
});

const cancelButton = document.getElementById(NEW_JOB_CANCEL_ID);
cancelButton.addEventListener('click', (_) => {
  window.location.href= HOMEPAGE_PATH;
});
