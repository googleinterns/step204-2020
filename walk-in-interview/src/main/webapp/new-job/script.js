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
const RESPONSE_ERROR = 'There was an error while creating' +
  'the job listing, please try submitting again';

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

  const jobDescription = document.getElementById('new-job-description');
  jobDescription.setAttribute('placeholder',
      STRINGS['new-job-description']);
  jobDescription.setAttribute('required', true);

  const jobAddress = document.getElementById('new-job-address');
  jobAddress.setAttribute('type', 'text');
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

  const durationTitle = document.getElementById('new-job-duration-title');
  durationTitle.innerText = STRINGS['new-job-duration-title'];
  addJobDurationOptions();

  const expiryTitle = document.getElementById('new-job-expiry-title');
  expiryTitle.innerText = STRINGS['new-job-expiry-title'];
  const expiryInput = document.getElementById('new-job-expiry');
  expiryInput.setAttribute('type', 'date');
  expiryInput.setAttribute('required', true);
  addJobExpiryLimits();

  setErrorMessage('', /** includes default msg */ false);
}

/**
 * Sets the error message according to the param.
 * @param {String} msg the message that the error div should display.
 * @param {boolean} includesDefault whether the deafult
 * message should be included.
 */
function setErrorMessage(msg, includesDefault) {
  console.log('set error: ', msg, ' & include default: ', includesDefault);
  document.getElementById('new-job-error-message').innerText =
    (includesDefault ? STRINGS['new-job-error-message'] + msg : msg);
}

/** Add the list of requirements that are stored in the database. */
function addRequirementsList() {
  const requirementsList = getRequirementsList();
  const requirementsListElement =
    document.getElementById('new-job-requirements-list');

  requirementsListElement.innerHTML = '';
  const requirementElementTemplate =
    document.getElementById('requirement-element-template');
  for (const key in requirementsList) {
    if (requirementsList.hasOwnProperty(key)) {
      const requirementElement = requirementElementTemplate
          .cloneNode( /** and child elements */ true);
      requirementElement.setAttribute('id', key);

      const checkbox = requirementElement.children[0];
      checkbox.setAttribute('id', key);
      checkbox.setAttribute('value', key);

      const label = requirementElement.children[1];
      label.setAttribute('for', key);
      label.innerText = requirementsList[key];

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
  const jobPaySelect = document.getElementById('new-job-pay-frequency');
  jobPaySelect.setAttribute('required', true);

  addSelectOptions(jobPaySelect, STRINGS['new-job-pay-frequency']);
}

/** Dynamically add the options for job duration. */
function addJobDurationOptions() {
  const jobDurationSelect = document.getElementById('new-job-duration');
  jobDurationSelect.setAttribute('required', true);

  addSelectOptions(jobDurationSelect, STRINGS['new-job-duration']);
}

/**
 * Add the keys and values from the options map to the select element.
 * @param {Element} select The select element.
 * @param {Map} options The map of options to be added.
 */
function addSelectOptions(select, options) {
  select.options.length = 0;
  select.options[0] = new Option('Select', '');
  select.options[0].setAttribute('disabled', true);

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

  const datePicker = document.getElementById('new-job-expiry');
  datePicker.setAttribute('min', min);
  datePicker.setAttribute('max', max);
}

/**
 * Gets job detail from user input.
 * @return {Object} containing the user inputs.
 */
function getJobDetailsFromUserInput() {
  const name = document.getElementById('new-job-title').value;
  const address = document.getElementById('new-job-address').value;
  const description = document.getElementById('new-job-description').value;
  const payFrequency = document.getElementById('new-job-pay-frequency').value;
  const payMin = document.getElementById('new-job-pay-min').valueAsNumber;
  const payMax = document.getElementById('new-job-pay-max').valueAsNumber;

  const requirementsCheckboxes =
    document.getElementsByName(STRINGS['new-job-requirements-list']);
  const requirementsList = [];
  requirementsCheckboxes.forEach(({checked, id}) => {
    if (checked) {
      requirementsList.push(id);
    }
  });

  const expiry = document.getElementById('new-job-expiry').valueAsNumber;
  const duration = document.getElementById('new-job-duration').value;

  const jobDetails = {
    jobTitle: name,
    jobLocation: {
      address: address,
      latitude: 1.3039, // TODO(issue/13): get these from places api
      longitude: 103.8358,
    },
    jobDescription: description,
    jobPay: {
      paymentFrequency: payFrequency,
      min: payMin,
      max: payMax,
    },
    requirements: requirementsList,
    postExpiryTimestamp: expiry,
    jobDuration: duration,
  };

  return jobDetails;
}

/**
 * Validates the user input
 * @return {boolean} depending on whether the input is valid or not.
 */
function validateRequiredUserInput() {
  // TODO(issue/19): add more validation checks
  const name = document.getElementById('new-job-title');
  const description = document.getElementById('new-job-description');
  const address = document.getElementById('new-job-address');
  const payFrequency = document.getElementById('new-job-pay-frequency').value;
  const payMin = document.getElementById('new-job-pay-min').valueAsNumber;
  const payMax = document.getElementById('new-job-pay-max').valueAsNumber;
  const duration = document.getElementById('new-job-duration').value;
  const expiry = document.getElementById('new-job-expiry').valueAsNumber;

  if (name.value === '') {
    setErrorMessage(name.placeholder,
        /** includes default msg */ true);
    return false;
  }

  if (description.value === '') {
    setErrorMessage(description.placeholder,
        /** includes default msg */ true);
    return false;
  }

  if (address.value === '') {
    setErrorMessage(address.placeholder,
        /** includes default msg */ true);
    return false;
  }

  if (payFrequency === '' || Number.isNaN(payMin) || Number.isNaN(payMax) ||
    payMin > payMax || payMin < 0 || payMax < 0) {
    setErrorMessage(document.getElementById('new-job-pay-title').textContent,
        /** includes default msg */ true);
    return false;
  }

  if (duration === '') {
    setErrorMessage(document.getElementById('new-job-duration-title')
        .textContent, /** includes default msg */ true);
    return false;
  }

  if (Number.isNaN(expiry)) {
    setErrorMessage(document.getElementById('new-job-expiry-title')
        .textContent, /** includes default msg */ true);
    return false;
  }

  return true;
}

const submitButton = document.getElementById('new-job-submit');
submitButton.addEventListener('click', (_) => {
  if (!validateRequiredUserInput()) {
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
        setErrorMessage('', /** include default msg */ false);
        window.location.href= HOMEPAGE_PATH;
      })
      .catch((error) => {
        setErrorMessage(RESPONSE_ERROR, /** include default msg */ false);
        console.log('error', error);
      });
});

const cancelButton = document.getElementById('new-job-cancel');
cancelButton.addEventListener('click', (_) => {
  window.location.href= HOMEPAGE_PATH;
});
