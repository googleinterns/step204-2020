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
import {AppStrings} from '../strings.en.js';

import {getRequirementsList, setErrorMessage} from '../common-functions.js';

const STRINGS = AppStrings['new-job'];
const COMMON_STRINGS = AppStrings['common'];
const HOMEPAGE_PATH = '../index.html';
const HOURS_PER_YEAR = 8760;
/* Note that is an approximate value */
const WEEKS_PER_YEAR = 52;
const MONTHS_PER_YEAR = 12;

/**
 * Note that this is needed because in JS we can hold bigger Integer
 * values than in Java.
 */
const JAVA_INTEGER_MAX_VALUE = Math.pow(2, 31) - 1;

let currErrorField;

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

  currErrorField = 'title';
  /* reset the error to make sure no error msg initially present */
  setErrorMessage(/* error div id */ 'error-message', /* msg */ '',
      /* includes default msg */ false);
}

/**
 * Sets the error message according to the param. This function
 * will also highlight the field where the error is.
 * @param {String} id The element in which the error is.
 * @param {String} msg The message that the error div should display.
 * @param {boolean} includesDefault Whether the deafult
 *    message should be included.
 */
function setErrorMessageAndField(id, msg, includesDefault) {
  setErrorMessage('error-message', msg, includesDefault);

  document.getElementById(currErrorField).style.backgroundColor = 'white';
  document.getElementById(id).style.backgroundColor = 'rgb(255,0,0, 0.1)';
  currErrorField = id;
}

/** Add the list of requirements that are stored in the database. */
function renderRequirementsList() {
  const requirementsList = getRequirementsList();
  const requirementsListElement =
    document.getElementById('requirements-list');

  /* reset the list so we don't render the same requirements twice */
  requirementsListElement.innerHTML = '';
  const requirementElementTemplate =
    document.getElementById('requirement-element-template');
  for (const key in requirementsList) {
    if (requirementsList.hasOwnProperty(key)) {
      const requirementElement = requirementElementTemplate
          .cloneNode( /* and child elements */ true);
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
    document.getElementsByName('requirements-list');
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
      region: findRegion(postalCode),
      lat: 1.3039, // TODO(issue/13): get these from places api
      lon: 103.8358,
    },
    jobDescription: description,
    jobPay: {
      paymentFrequency: payFrequency,
      min: payMin,
      max: payMax,
      annualMax: calculateAnnualMax(payMax, payFrequency),
    },
    requirements: requirementsList,
    postExpiryTimestamp: expiry,
    jobDuration: duration,
  };

  return jobDetails;
}

/**
 * Returns the Singapore region based on the postal code.
 * This is done by taking the first two digits of the postal
 * code and seeing which region it corresponds to.
 * Central: 01-45
 * West:  58-71
 * East: 46-52, 81
 * North: 72-73, 75-80
 * North-East: 53-57, 82
 * @param {String} postalCode The Singapore postal code.
 * @return {String} The Singapore region.
 */
function findRegion(postalCode) {
  /*
   * We have made the assumption that if the user's
   * postal code region is 01, they would have written
   * 01xxx rather than 1xxx.
   */
  const digits = parseInt(postalCode.substring(0, 2));

  if (digits >= 1 && digits <= 45) {
    return 'CENTRAL';
  } else if (digits >= 58 && digits <= 71) {
    return 'WEST';
  } else if ((digits >= 46 && digits <= 52) ||
      digits === 81) {
    return 'EAST';
  } else if ((digits >= 72 && digits <= 73) ||
      (digits >= 75 && digits <= 80)) {
    return 'NORTH';
  } else if ((digits >= 53 && digits <= 57) ||
      digits === 82) {
    return 'NORTH_EAST';
  }
  setErrorMessageAndField(/* error element id */ 'postal-code',
      /* msg */ postalCode.placeholder, /* includes default msg */ true);
  throw new Error('invalid postal code');
}

/**
 * This function calculates and returns the annual pay depending
 * on the maximum pay and the frequency.
 * @param {int} max the upper limit on the pay.
 * @param {String} frequency how often the employee will be paid.
 * @return {int} the annual pay.
 */
function calculateAnnualMax(max, frequency) {
  switch (frequency) {
    case 'HOURLY':
      return max * HOURS_PER_YEAR;
    case 'WEEKLY':
      return max * WEEKS_PER_YEAR;
    case 'MONTHLY':
      return max * MONTHS_PER_YEAR;
    case 'YEARLY':
      return max;
  };
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
  const postalCode = document.getElementById('postal-code');
  const payFrequency = document.getElementById('pay-frequency').value;
  const payMin = document.getElementById('pay-min').valueAsNumber;
  const payMax = document.getElementById('pay-max').valueAsNumber;
  const duration = document.getElementById('duration').value;
  const expiry = document.getElementById('expiry').valueAsNumber;

  if (name.value === '') {
    setErrorMessageAndField( /* error element id */ 'title',
        /* msg */ name.placeholder, /* includes default msg */ true);
    return false;
  }

  if (description.value === '') {
    setErrorMessageAndField(/* error element id */ 'description',
        /* msg */ description.placeholder, /* includes default msg */ true);
    return false;
  }

  if (address.value === '') {
    setErrorMessageAndField(/* error element id */ 'address',
        /* msg */ address.placeholder, /* includes default msg */ true);
    return false;
  }

  /*
   * The first two digits of the postal code must be numbers within
   * the below range because those two digits correspond to the location's
   * district, which indicates its region in Singapore.
   */
  if (postalCode.value === '' ||
    (parseInt(postalCode.value[0]) > JAVA_INTEGER_MAX_VALUE) ||
    (parseInt(postalCode.value[1]) > JAVA_INTEGER_MAX_VALUE)) {
    setErrorMessageAndField(/* error element id */ 'postal-code',
        /* msg */ postalCode.placeholder, /* includes default msg */ true);
    return false;
  }

  if (payFrequency === '') {
    setErrorMessageAndField(/* error element id */'pay-frequency',
        /* msg */ document.getElementById('pay-title').textContent,
        /* includes default msg */ true);
    return false;
  }

  if (Number.isNaN(payMin) || (payMin > JAVA_INTEGER_MAX_VALUE) || payMin < 0) {
    setErrorMessageAndField(/* error element id */ 'pay-min',
        /* msg */ document.getElementById('pay-title').textContent,
        /* includes default msg */ true);
    return false;
  }

  if (Number.isNaN(payMax) || (payMax > JAVA_INTEGER_MAX_VALUE) ||
    payMin > payMax || payMax < 0) {
    setErrorMessageAndField(/* error element id */ 'pay-max',
        /* msg */ document.getElementById('pay-title').textContent,
        /* includes default msg */ true);
    return false;
  }

  if (duration === '') {
    setErrorMessageAndField(/* error element id */ 'duration',
        /* msg */ document.getElementById('duration-title').textContent,
        /* includes default msg */ true);
    return false;
  }

  if (Number.isNaN(expiry)) {
    setErrorMessageAndField(/* error element id */ 'expiry',
        /* msg */ document.getElementById('expiry-title').textContent,
        /* includes default msg */ true);
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
        /* reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* error div id */ 'error-message', /* msg */ '',
            /* include default msg */ false);
        window.location.href= HOMEPAGE_PATH;
      })
      .catch((error) => {
        setErrorMessage(/* error div id */ 'error-message',
            /* msg */ COMMON_STRINGS['creating-job-error-message'],
            /* include default msg */ false);
        console.log('error creating job listing', error);
      });
});

const cancelButton = document.getElementById('cancel');
cancelButton.addEventListener('click', (_) => {
  window.location.href= HOMEPAGE_PATH;
});
