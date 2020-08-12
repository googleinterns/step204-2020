/**
 * This file is specific to account/create-account/business/account-info/index.html. 
 * It renders the fields on the page dynamically.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../../../../strings.en.js';
import {API} from '../../../../apis.js';
import {TYPE_BUSINESS, setErrorMessage} from '../../../../common-functions.js';

const HOMEPAGE_PATH = '../../../../index.html';
const STRINGS = AppStrings['create-business-account'];
const ACCOUNT_STRINGS = AppStrings['create-account'];
const BAD_REQUEST_STATUS_CODE = 400;

window.onload = () => {
  renderPageElements();
};

function renderPageElements() {
  const backButton = document.getElementById('back');
  backButton.innerText = ACCOUNT_STRINGS['back'];

  const submitButton = document.getElementById('submit');
  submitButton.setAttribute('value', ACCOUNT_STRINGS['submit']);
  submitButton.setAttribute('type', 'submit');

  const nameLabel = document.getElementById('name-label');
  nameLabel.innerText = STRINGS['name'];

  const name = document.getElementById('name');
  name.setAttribute('type',  'text');
}

function getBusinessDetailsFromUserInput() {
  const businessName = document.getElementById('name').value;

  const businessDetails = {
    userType: TYPE_BUSINESS,
    name: businessName,
    jobs: [], // empty job list when the account is newly created
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
  const name = document.getElementById('name').value;

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

  fetch(API['create-business-account'], {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(accountDetails),
  })
      .then((response) => {
        if (response.status == BAD_REQUEST_STATUS_CODE) {
          setErrorMessage(/* errorMessageElementId= */'error-message',
              /* msg= */ ACCOUNT_STRINGS['create-account-error-message'],
              /* includesDefault= */false);
          throw new Error(ACCOUNT_STRINGS['create-account-error-message']);
        }

        /** reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* errorMessageElementId= */'error-message',
            /* msg= */ '', /* includesDefault= */false);
        window.location.href = HOMEPAGE_PATH;
      })
      .catch((error) => {
        // Not the server response error already caught and thrown
        if (error.message != ACCOUNT_STRINGS['create-account-error-message']) {
          console.log('error', error);

          setErrorMessage(/* errorMessageElementId= */'error-message',
              /* msg= */ ACCOUNT_STRINGS['error-message'],
              /* includesDefault= */false);
        }
      });
});
