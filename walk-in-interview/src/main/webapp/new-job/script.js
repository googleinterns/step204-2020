/**
 * This file is specific to index.html for new-job. It renders the fields on the
 * page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from './strings.en.js';

const STRINGS = AppStrings['new-job'];
const HOMEPAGE_PATH = '../homepage/index.html';
const RESPONSE_ERROR = 'There was an error while creating' +
  'the job listing, please try submitting again';

window.onload = () => {
  renderJobPageElements();
};

/** Adds all the titles to the fields on this page. */
function renderJobPageElements() {
  const cancelButton = document.getElementById('cancel');
  cancelButton.setAttribute('value', STRINGS['cancel']);
  cancelButton.setAttribute('type', 'reset');

  const jobPageTitle = document.getElementById('page-title');
  jobPageTitle.innerText = STRINGS['page-title'];

  const submitButton = document.getElementById('submit');
  submitButton.setAttribute('value', STRINGS['submit']);
  submitButton.setAttribute('type', 'submit');

  const jobTitle = document.getElementById('title');
  jobTitle.setAttribute('type', 'text');
  jobTitle.setAttribute('placeholder', STRINGS['title']);
  jobTitle.setAttribute('required', true);

  const jobDescription = document.getElementById('description');
  jobDescription.setAttribute('placeholder',
      STRINGS['description']);
  jobDescription.setAttribute('required', true);

  const jobAddress = document.getElementById('address');
  jobAddress.setAttribute('placeholder', STRINGS['address']);
  jobAddress.setAttribute('required', true);

  const postalCode = document.getElementById('postal-code');
  postalCode.setAttribute('type', 'text');
  postalCode.setAttribute('placeholder', STRINGS['postal-code']);
  postalCode.setAttribute('required', true);

  const requirementsTitle =
    document.getElementById('requirements-title');
  requirementsTitle.innerText = STRINGS['requirements-title'];
  renderRequirementsList();

  const payTitle = document.getElementById('pay-title');
  payTitle.innerText = STRINGS['pay-title'];
  renderJobPayFrequencyOptions();

  const payMin = document.getElementById('pay-min');
  payMin.setAttribute('type', 'number');
  payMin.setAttribute('placeholder', STRINGS['pay-min']);
  payMin.setAttribute('required', true);

  const payMax = document.getElementById('pay-max');
  payMax.setAttribute('type', 'number');
  payMax.setAttribute('placeholder', STRINGS['pay-max']);
  payMax.setAttribute('required', true);

  const durationTitle = document.getElementById('duration-title');
  durationTitle.innerText = STRINGS['duration-title'];
  renderJobDurationOptions();

  const expiryTitle = document.getElementById('expiry-title');
  expiryTitle.innerText = STRINGS['expiry-title'];
  const expiryInput = document.getElementById('expiry');
  expiryInput.setAttribute('type', 'date');
  expiryInput.setAttribute('required', true);
  renderJobExpiryLimits();

  /** reset the error to make sure no error msg initially present */
  setErrorMessage(/* msg */ '', /** includes default msg */ false);
}

/**
 * Sets the error message according to the param.
 * @param {String} msg the message that the error div should display.
 * @param {boolean} includesDefault whether the deafult
 * message should be included.
 */
function setErrorMessage(msg, includesDefault) {
  document.getElementById('error-message').innerText =
    (includesDefault ? STRINGS['error-message'] + msg : msg);
}

/** Add the list of requirements that are stored in the database. */
function renderRequirementsList() {
  const requirementsList = getRequirementsList();
  const requirementsListElement =
    document.getElementById('requirements-list');

  /** reset the list so we don't render the same requirements twice */
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
    'O_LEVEL': 'O Level',
    'LANGUAGE_ENGLISH': 'English',
    'DRIVING_LICENSE_C': 'Category C Driving License',
  };
}

/** Dynamically add the options for job pay frequency. */
function renderJobPayFrequencyOptions() {
  const jobPaySelect = document.getElementById('pay-frequency');
  jobPaySelect.setAttribute('required', true);

  renderSelectOptions(jobPaySelect, STRINGS['pay-frequency']);
}

/** Dynamically add the options for job duration. */
function renderJobDurationOptions() {
  const jobDurationSelect = document.getElementById('duration');
  jobDurationSelect.setAttribute('required', true);

  renderSelectOptions(jobDurationSelect, STRINGS['duration']);
}

/**
 * Add the keys and values from the options map to the select element.
 * @param {Element} select The select element.
 * @param {Map} options The map of options to be added.
 */
function renderSelectOptions(select, options) {
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
function renderJobExpiryLimits() {
  const date = new Date();
  const min = date.toISOString().substr(0, 10);
  date.setFullYear(date.getFullYear() + 1);
  const max = date.toISOString().substr(0, 10);

  const datePicker = document.getElementById('expiry');
  datePicker.setAttribute('min', min);
  datePicker.setAttribute('max', max);
}

/**
 * Gets job detail from user input.
 * @return {Object} containing the user inputs.
 */
function getJobDetailsFromUserInput() {
  const name = document.getElementById('title').value;
  const description = document.getElementById('description').value;
  const address = document.getElementById('address').value;
  const postalCode = document.getElementById('postal-code').value;
  const payFrequency = document.getElementById('pay-frequency').value;
  const payMin = document.getElementById('pay-min').valueAsNumber;
  const payMax = document.getElementById('pay-max').valueAsNumber;

  const requirementsCheckboxes =
    document.getElementsByName(STRINGS['requirements-list']);
  const requirementsList = [];
  requirementsCheckboxes.forEach(({checked, id}) => {
    if (checked) {
      requirementsList.push(id);
    }
  });

  const expiry = document.getElementById('expiry').valueAsNumber;
  const duration = document.getElementById('duration').value;

  const jobDetails = {
    jobTitle: name,
    jobLocation: {
      address: address,
      postalCode: postalCode,
      lat: 1.3039, // TODO(issue/13): get these from places api
      lon: 103.8358,
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
  const name = document.getElementById('title');
  const description = document.getElementById('description');
  const address = document.getElementById('address');
  const postalCode = document.getElementById('postal-code').value;
  const payFrequency = document.getElementById('pay-frequency').value;
  const payMin = document.getElementById('pay-min').valueAsNumber;
  const payMax = document.getElementById('pay-max').valueAsNumber;
  const duration = document.getElementById('duration').value;
  const expiry = document.getElementById('expiry').valueAsNumber;

  if (name.value === '') {
    setErrorMessage(/* msg */ name.placeholder,
        /** includes default msg */ true);
    return false;
  }

  if (description.value === '') {
    setErrorMessage(/* msg */ description.placeholder,
        /** includes default msg */ true);
    return false;
  }

  if (address.value === '' || postalCode === '') {
    setErrorMessage(/* msg */ address.placeholder,
        /** includes default msg */ true);
    return false;
  }

  if (payFrequency === '' || Number.isNaN(payMin) || Number.isNaN(payMax) ||
    payMin > payMax || payMin < 0 || payMax < 0) {
    setErrorMessage(/* msg */ document.getElementById('pay-title')
        .textContent, /** includes default msg */ true);
    return false;
  }

  if (duration === '') {
    setErrorMessage(/* msg */ document.getElementById('duration-title')
        .textContent, /** includes default msg */ true);
    return false;
  }

  if (Number.isNaN(expiry)) {
    setErrorMessage(/* msg */ document.getElementById('expiry-title')
        .textContent, /** includes default msg */ true);
    return false;
  }

  return true;
}

const submitButton = document.getElementById('submit');
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
        /** reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* msg */ '', /** include default msg */ false);
        window.location.href= HOMEPAGE_PATH;
      })
      .catch((error) => {
        setErrorMessage(/* msg */ RESPONSE_ERROR,
            /** include default msg */ false);
        console.log('error', error);
      });
});

const cancelButton = document.getElementById('cancel');
cancelButton.addEventListener('click', (_) => {
  window.location.href= HOMEPAGE_PATH;
});
