/**
 * This file is specific to new-job.html. It renders the fields on the
 * page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dnamic imports
 */
import {AppStrings} from './strings.en.js';

const STRINGS = AppStrings['new-job'];
const HOMEPAGE_PATH = '../index.html';

window.onload = () => {
  addJobPageElements();
};

/** Adds all the titles to the fields on this page. */
function addJobPageElements() {
  const cancelButton = document.getElementById('new-job-cancel');
  cancelButton.setAttribute('value', STRINGS['new-job-cancel']);
  cancelButton.setAttribute('type', 'reset');

  const jobPageTitle = document.getElementById('new-job-page-title');
  jobPageTitle.innerText = STRINGS['new-job-page-title'];

  const submitButton = document.getElementById('new-job-submit');
  submitButton.setAttribute('value', STRINGS['new-job-submit']);
  submitButton.setAttribute('type', 'submit');

  const jobTitle = document.getElementById('new-job-title');
  jobTitle.setAttribute('type', 'text');
  jobTitle.setAttribute('placeholder', STRINGS['new-job-title']);
  jobTitle.setAttribute('required', true);

  const jobDescription =
    document.getElementById('new-job-description');
  jobDescription.setAttribute('placeholder',
      STRINGS['new-job-description']);
  jobDescription.setAttribute('required', true);

  const jobAddress = document.getElementById('new-job-address');
  jobAddress.setAttribute('placeholder', STRINGS['new-job-address']);
  jobAddress.setAttribute('required', true);

  const requirementsTitle =
    document.getElementById('new-job-requirements-title');
  requirementsTitle.innerText = STRINGS['new-job-requirements-title'];
  addRequirementsList();

  const payTitle = document.getElementById('new-job-pay-title');
  payTitle.innerText = STRINGS['new-job-pay-title'];
  addJobPayFrequencyOptions();

  const payMin = document.getElementById('new-job-pay-min');
  payMin.setAttribute('type', 'number');
  payMin.setAttribute('placeholder', STRINGS['new-job-pay-min']);
  payMin.setAttribute('required', true);

  const payMax = document.getElementById('new-job-pay-max');
  payMax.setAttribute('type', 'number');
  payMax.setAttribute('placeholder', STRINGS['new-job-pay-max']);
  payMax.setAttribute('required', true);

  const durationTitle =
    document.getElementById('new-job-duration-title');
  durationTitle.innerText = STRINGS['new-job-duration-title'];
  addJobDurationOptions();

  const expiryTitle = document.getElementById('new-job-expiry-title');
  expiryTitle.innerText = STRINGS['new-job-expiry-title'];
  const expiryInput =
    document.getElementById('new-job-expiry');
  expiryInput.setAttribute('type', 'date');
  expiryInput.setAttribute('required', true);
  addJobExpiryLimits();
}

/** Add the list of requirements that are stored in the database. */
function addRequirementsList() {
  const requirementsList = getRequirementsList();
  const requirementsListElement =
    document.getElementById('new-job-requirements-list');

  requirementsListElement.innerHTML = '';
  for (const key in requirementsList) {
    if (requirementsList.hasOwnProperty(key)) {
      const requirementElement = document.createElement('li');
      requirementElement.setAttribute('id', key);

      const checkbox = document.createElement('input');
      checkbox.setAttribute('type', 'checkbox');
      checkbox.setAttribute('id', key);
      checkbox.setAttribute('name',
          STRINGS['new-job-requirements-list']);
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
  const jobPaySelect =
    document.getElementById('new-job-pay-frequency');
  jobPaySelect.setAttribute('required', true);
  jobPaySelect.options.length = 0;

  jobPaySelect.options[0] = new Option('Select', '');
  jobPaySelect.options[0].setAttribute('disabled', true);
  addSelectOptions(jobPaySelect, STRINGS['new-job-pay-frequency']);
}

/** Dynamically add the options for job duration. */
function addJobDurationOptions() {
  const jobDurationSelect =
    document.getElementById('new-job-duration');
  jobDurationSelect.options.length = 0;

  jobDurationSelect.options[0] = new Option('Other', '');
  addSelectOptions(jobDurationSelect,
      STRINGS['new-job-duration']);
}

/**
 * Add the keys and values from the options map to the select element.
 * @param {Element} select The select element.
 * @param {Map} options The map of options to be added.
 */
function addSelectOptions(select, options) {
  for (const key in options) {
    if (options.hasOwnProperty(key)) {
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

  const datePicker =
    document.getElementById('new-job-expiry');
  datePicker.setAttribute('min', min);
  datePicker.setAttribute('max', max);
}

/**
 * Gets job detail from user input.
 * @return {Object} containing the user inputs.
 */
function getJobDetailsFromUserInput() {
  const name = document.getElementById('new-job-title').value;
  const address =
    document.getElementById('new-job-address').value;
  const description =
    document.getElementById('new-job-description').value;
  const payFrequency =
    document.getElementById('new-job-pay-frequency').value;
  const payMin =
    document.getElementById('new-job-pay-min').valueAsNumber;
  const payMax =
    document.getElementById('new-job-pay-max').valueAsNumber;

  const requirementsCheckboxes =
    document.getElementsByName(STRINGS['new-job-requirements-list']);
  const requirementsList = [];
  requirementsCheckboxes.forEach(({checked, id}) => {
    if (checked) {
      requirementsList.push(id);
    }
  });

  const expiry =
    document.getElementById('new-job-expiry')
        .valueAsNumber;
  const duration =
    document.getElementById('new-job-duration').value;

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
  const name = document.getElementById('new-job-title').value;
  const address =
    document.getElementById('new-job-address').value;
  const description =
    document.getElementById('new-job-description').value;
  const payFrequency =
    document.getElementById('new-job-pay-frequency').value;
  const payMin =
    document.getElementById('new-job-pay-min').valueAsNumber;
  const payMax =
    document.getElementById('new-job-pay-max').valueAsNumber;
  const expiry =
    document.getElementById('new-job-expiry')
        .valueAsNumber;

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

const submitButton = document.getElementById('new-job-submit');
submitButton.addEventListener('click', (_) => {
  if (!validateRequiredUserInput()) {
    document.getElementById('new-job-error-message').innerText =
      STRINGS['new-job-error-message'].message;
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
        document.getElementById('new-job-error-message').innerText = '';
        window.location.href= HOMEPAGE_PATH;
      })
      .catch((error) => {
        document.getElementById('new-job-error-message').innerText =
          STRINGS['new-job-error-message'].message;
        console.log('error', error);
      });
});

const cancelButton = document.getElementById('new-job-cancel');
cancelButton.addEventListener('click', (_) => {
  window.location.href= HOMEPAGE_PATH;
});
