/**
 * This file is specific to account/business/edit/index.html. 
 * It renders the fields on the page dynamically.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../../../strings.en.js';
import {API} from '../../../apis.js';
import {BUSINESS_ID_PARAM, BUSINESS_DETAILS_PARAM} 
  from '../../../common-functions.js';

const HOMEPAGE_PATH = '../../../index.html';
const STRINGS = AppStrings['business'];
const ACCOUNT_STRINGS = AppStrings['account'];
const BAD_REQUEST_STATUS_CODE = 400;

window.onload = () => {
  renderPageElements();
};

function renderPageElements() {
  const businessName = getBusinessDetails().name;

  const backButton = document.getElementById('back');
  backButton.innerText = ACCOUNT_STRINGS['back'];

  const submitButton = document.getElementById('submit');
  submitButton.setAttribute('value', ACCOUNT_STRINGS['submit']);
  submitButton.setAttribute('type', 'submit');

  const nameLabel = document.getElementById('name-label');
  nameLabel.innerText = STRINGS['name'];

  const name = document.getElementById('name');
  name.setAttribute('type',  'text');
  name.setAttribute('value', businessName);
}

/**
 * Gets the businessId from the url.
 * @return {String} The applicantId.
 */
function getBusinessId() {
  const queryString = window.location.search;

  const urlParams = new URLSearchParams(queryString);
  if (urlParams === '') {
    throw new Error('url params should not be empty');
  }

  return urlParams.get(BUSINESS_ID_PARAM);
}

/**
 * Gets the business details from the url.
 * @return {JSON} The business detail json.
 */
function getBusinessDetails() {
  const queryString = window.location.search;

  const urlParams = new URLSearchParams(queryString);
  if (urlParams === '') {
    throw new Error('url params should not be empty');
  }

  return JSON.parse(urlParams.get(BUSINESS_DETAILS_PARAM));
}

function getBusinessDetailsFromUserInput() {
  const businessId = getBusinessId();
  const businessName = document.getElementById('name').value;

  const jobsList = getBusinessDetails().jobs;

  const businessDetails = {
    accountId: businessId,
    name: businessName,
    jobs: jobsList,
  };

  return businessDetails;
}

/**
* Validates the user input.
* Shows error message on the webpage if there is field with invalid input.
*
* @return {boolean} depending on whether the input is valid or not.
*/
function validateRequiredUserInput() {
  const businessId = getApplicantId();
  const name = document.getElementById('name').value;

  if (businessId === '') {
    setErrorMessage(/* errorMessageElementId= */'error-message',
        /* msg= */ 'Empty Business Id found.',
        /* includesDefault= */false);
    return false;
  }

  if (name === '') {
    setErrorMessage(/* errorMessageElementId= */'error-message',
        /* msg= */ STRINGS['name']);
    return false;
  }

  return true;
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = HOMEPAGE_PATH;
});

const submitButton = document.getElementById('submit');
submitButton.addEventListener('click', (_) => {
  if (!validateRequiredUserInput()) {
    return;
  }

  const accountDetails = getBusinessDetailsFromUserInput();

  fetch(API['update-business-account'], {
    method: 'PATCH',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(accountDetails),
  })
      .then((response) => {
        if (response.status == BAD_REQUEST_STATUS_CODE) {
          setErrorMessage(/* errorMessageElementId= */'error-message',
              /* msg= */ STRINGS['update-error-message'],
              /* includesDefault= */false);
          throw new Error(STRINGS['update-error-message']);
        }

        /** reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* errorMessageElementId= */'error-message',
            /* msg= */ '', /* includesDefault= */false);
        window.location.href = HOMEPAGE_PATH;
      })
      .catch((error) => {
        // Not the server response error already caught and thrown
        if (error.message != STRINGS['update-error-message']) {
          setErrorMessage(/* errorMessageElementId= */'error-message',
              /* msg= */ STRINGS['error-message'],
              /* includesDefault= */false);
          console.log('error', error);
        }
      });
});
