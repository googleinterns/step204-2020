/**
 * This file is specific to new-job.html. It renders the fields on the
 * page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

import {AppStrings} from './strings.en.js';

const NEW_JOB_STRINGS = AppStrings['new-job'];
const HOMEPAGE_PATH = '../index.html';

window.onload = () => {
  addJobPageElements();
};

/** Adds all the titles to the fields on this page. */
function addJobPageElements() {
  const cancelButton = document.getElementById(NEW_JOB_STRINGS['cancel'].id);
  cancelButton.setAttribute('value', NEW_JOB_STRINGS['cancel'].title);
  cancelButton.setAttribute('type', 'reset');

  const jobPageTitle = document.getElementById(NEW_JOB_STRINGS['page'].id);
  jobPageTitle.innerText = NEW_JOB_STRINGS['page'].title;

  const submitButton = document.getElementById(NEW_JOB_STRINGS['submit'].id);
  submitButton.setAttribute('value', NEW_JOB_STRINGS['submit'].title);
  submitButton.setAttribute('type', 'submit');

  const jobTitle = document.getElementById(NEW_JOB_STRINGS['job-title'].id);
  jobTitle.setAttribute('type', 'text');
  jobTitle.setAttribute('placeholder', NEW_JOB_STRINGS['job-title'].title);
  jobTitle.setAttribute('required', true);

  const jobDescription =
    document.getElementById(NEW_JOB_STRINGS['job-description'].id);
  jobDescription.setAttribute('placeholder',
      NEW_JOB_STRINGS['job-description'].title);
  jobDescription.setAttribute('required', true);

  const jobAddress = document.getElementById(NEW_JOB_STRINGS['job-address'].id);
  jobAddress.setAttribute('placeholder', NEW_JOB_STRINGS['job-address'].title);
  jobAddress.setAttribute('required', true);

  const requirementsTitle =
    document.getElementById(NEW_JOB_STRINGS['job-requirements'].id);
  requirementsTitle.innerText = NEW_JOB_STRINGS['job-requirements'].title;
  addRequirementsList();

  const payTitle = document.getElementById(NEW_JOB_STRINGS['job-pay'].id);
  payTitle.innerText = NEW_JOB_STRINGS['job-pay'].title;
  addJobPayFrequencyOptions();

  const payMin = document.getElementById(NEW_JOB_STRINGS['job-pay-min'].id);
  payMin.setAttribute('type', 'number');
  payMin.setAttribute('placeholder', NEW_JOB_STRINGS['job-pay-min'].title);
  payMin.setAttribute('required', true);

  const payMax = document.getElementById(NEW_JOB_STRINGS['job-pay-max'].id);
  payMax.setAttribute('type', 'number');
  payMax.setAttribute('placeholder', NEW_JOB_STRINGS['job-pay-max'].title);
  payMax.setAttribute('required', true);

  const durationTitle =
    document.getElementById(NEW_JOB_STRINGS['job-duration'].id);
  durationTitle.innerText = NEW_JOB_STRINGS['job-duration'].title;
  addJobDurationOptions();

  const expiryTitle = document.getElementById(NEW_JOB_STRINGS['job-expiry'].id);
  expiryTitle.innerText = NEW_JOB_STRINGS['job-expiry'].title;
  const expiryInput =
    document.getElementById(NEW_JOB_STRINGS['job-expiry-picker'].id);
  expiryInput.setAttribute('type', 'date');
  expiryInput.setAttribute('name', NEW_JOB_STRINGS['job-expiry-picker'].id);
  expiryInput.setAttribute('required', true);
  addJobExpiryLimits();
}

/** Add the list of requirements that are stored in the database. */
function addRequirementsList() {
  const requirementsList = getRequirementsList();
  const requirementsListElement =
    document.getElementById(NEW_JOB_STRINGS['job-requirements-list'].id);

  requirementsListElement.innerHTML = '';
  for (const key in requirementsList) {
    if ({}.hasOwnProperty.call(requirementsList, key)) {
      const requirementElement = document.createElement('li');
      requirementElement.setAttribute('id', key);

      const checkbox = document.createElement('input');
      checkbox.setAttribute('type', 'checkbox');
      checkbox.setAttribute('id', key);
      checkbox.setAttribute('name',
          NEW_JOB_STRINGS['job-requirements-list'].name);
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
    document.getElementById(NEW_JOB_STRINGS['job-pay-frequency'].id);
  jobPaySelect.setAttribute('required', true);
  jobPaySelect.options.length = 0;

  jobPaySelect.options[0] = new Option('Select', '');
  jobPaySelect.options[0].setAttribute('disabled', true);
  addSelectOptions(jobPaySelect, NEW_JOB_STRINGS['job-pay-frequency'].values);
}

/** Dynamically add the options for job duration. */
function addJobDurationOptions() {
  const jobDurationSelect =
    document.getElementById(NEW_JOB_STRINGS['job-duration-select'].id);
  jobDurationSelect.options.length = 0;

  jobDurationSelect.options[0] = new Option('Other', '');
  addSelectOptions(jobDurationSelect,
      NEW_JOB_STRINGS['job-duration-select'].values);
}

/**
 * Add the keys and values from the options map to the select element.
 * @param {Element} select The select element.
 * @param {Map} options The map of options to be added.
 */
function addSelectOptions(select, options) {
  for (const key in options) {
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

  const datePicker =
    document.getElementById(NEW_JOB_STRINGS['job-expiry-picker'].id);
  datePicker.setAttribute('min', min);
  datePicker.setAttribute('max', max);
}

/**
 * Gets job detail from user input.
 * @return {Object} containing the user inputs.
 */
function getJobDetailsFromUserInput() {
  const name = document.getElementById(NEW_JOB_STRINGS['job-title'].id).value;
  const address =
    document.getElementById(NEW_JOB_STRINGS['job-address'].id).value;
  const description =
    document.getElementById(NEW_JOB_STRINGS['job-description'].id).value;
  const payFrequency =
    document.getElementById(NEW_JOB_STRINGS['job-pay-frequency'].id).value;
  const payMin =
    document.getElementById(NEW_JOB_STRINGS['job-pay-min'].id).valueAsNumber;
  const payMax =
    document.getElementById(NEW_JOB_STRINGS['job-pay-max'].id).valueAsNumber;

  const requirementsCheckboxes =
    document.getElementsByName(NEW_JOB_STRINGS['job-requirements-list'].name);
  const requirementsList = [];
  requirementsCheckboxes.forEach(({checked, id}) => {
    if (checked) {
      requirementsList.push(id);
    }
  });

  const expiry =
    document.getElementById(NEW_JOB_STRINGS['job-expiry-picker'].id)
        .valueAsNumber;
  const duration =
    document.getElementById(NEW_JOB_STRINGS['job-duration-select'].id).value;

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
  const name = document.getElementById(NEW_JOB_STRINGS['job-title'].id).value;
  const address =
    document.getElementById(NEW_JOB_STRINGS['job-address'].id).value;
  const description =
    document.getElementById(NEW_JOB_STRINGS['job-description'].id).value;
  const payFrequency =
    document.getElementById(NEW_JOB_STRINGS['job-pay-frequency'].id).value;
  const payMin =
    document.getElementById(NEW_JOB_STRINGS['job-pay-min'].id).valueAsNumber;
  const payMax =
    document.getElementById(NEW_JOB_STRINGS['job-pay-max'].id).valueAsNumber;
  const expiry =
    document.getElementById(NEW_JOB_STRINGS['job-expiry-picker'].id)
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

const submitButton = document.getElementById(NEW_JOB_STRINGS['submit'].id);
submitButton.addEventListener('click', (_) => {
  if (!validateRequiredUserInput()) {
    document.getElementById(NEW_JOB_STRINGS['error'].id).innerText =
      NEW_JOB_STRINGS['error'].message;
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
        document.getElementById(NEW_JOB_STRINGS['error'].id).innerText = '';
        window.location.href= HOMEPAGE_PATH;
      })
      .catch((error) => {
        document.getElementById(NEW_JOB_STRINGS['error'].id).innerText =
          NEW_JOB_STRINGS['error'].message;
        console.log('error', error);
      });
});

const cancelButton = document.getElementById(NEW_JOB_STRINGS['cancel'].id);
cancelButton.addEventListener('click', (_) => {
  window.location.href= HOMEPAGE_PATH;
});
