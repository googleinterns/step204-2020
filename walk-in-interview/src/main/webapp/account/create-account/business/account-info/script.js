/**
 * This file is specific to account/create-account/business/account-info/index.html. 
 * It renders the fields on the page dynamically.
 */

// TODO(issue/21): get the language from the browser
const CURRENT_LOCALE = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../../../../strings.en.js';
import {API} from '../../../../apis.js';
import {Auth} from '../../../firebase-auth.js';
import {USER_TYPE_COOKIE_PARAM, USER_TYPE_BUSINESS,
  setCookie, setErrorMessage} from '../../../../common-functions.js';

const HOMEPAGE_PATH = '../../../../index.html';
const STRINGS = AppStrings['create-business-account'];
const ACCOUNT_STRINGS = AppStrings['create-account'];
const BAD_REQUEST_STATUS_CODE = 400;

window.onload = () => {
  Auth.subscribeToUserAuthenticationChanges(
    onLogIn, onLogOut, onLogInFailure, onLogOutFailure);
  renderPageElements();
};

/**
 * What to do after the user signed in and the session cookie is created.
 */
function onLogIn() {
  // TODO(issue/100): set the cookie at the server side instead
  setCookie(USER_TYPE_COOKIE_PARAM, USER_TYPE_BUSINESS);
}

function onLogOut() {
  // TODO(issue/101): Display button according to log in status;
}

function onLogInFailure() {
  // TODO(issue/101): Display button according to log in status;
}


function onLogOutFailure() {
  // TODO(issue/101): Display button according to log in status;
}


/** Adds all the text to the fields on this page. */
function renderPageElements() {
  const submitButton = document.getElementById('submit');
  submitButton.setAttribute('value', ACCOUNT_STRINGS['submit']);
  submitButton.setAttribute('type', 'submit');

  const nameLabel = document.getElementById('name-label');
  nameLabel.innerText = STRINGS['name'];

  const name = document.getElementById('name');
  name.setAttribute('type',  'text');
}

/**
 * Gets account detail from user input.
 * 
 * @return {Object} Business account object containing the user inputs.
 */
function getBusinessDetailsFromUserInput() {
  const businessName = document.getElementById('name').value.trim();

  const businessDetails = {
    userType: USER_TYPE_BUSINESS,
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
  const name = document.getElementById('name').value.trim();

  if (name === '') {
    setErrorMessage(/* errorMessageElementId= */'error-message',
        /* msg= */ STRINGS['name']);
    return false;
  }

  return true;
}

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
