/**
 * This file is specific to account/applicant-account.html. 
 * It renders the fields on the page dynamically.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../strings.en.js';
import { get } from 'selenium-webdriver/http';

const HOMEPAGE_PATH = '../index.html';
const STRINGS = AppStrings['applicant'];

window.onload = () => {
  let accountId = getId();
  renderPageElements(accountId);
};

/** Gets the ID of this account. */
function getId() {
  // TODO

}

/**
 * Adds all the text element in the page.
 * 
 * @param {String} accountId Id of this account.
 */
function renderPageElements(accountId) {
  const backButton = document.getElementById('back');
  backButton.innerText = STRINGS['back'];

  const editButton = document.getElementById('edit');
  editButton.innerText = STRINGS['edit'];

  var accountDetails = getAccountDetails(accountId);

  const name = document.getElementById('name');
  name.innerText = accountDetails.name;

  const postalCode = document.getElementById('postal-code');
  postalCode.innerText = accountDetails.postalCode;

  const address = document.getElementById('address');
  address.innerText = accountDetails.address;

  renderRequirements(accountDetails.requirements);
}

/**
 * Gets the account detail from the database
 * 
 * @param {String} accountId Id of this account.
 * @returns Detail json.
 */
function getAccountDetails(accountId) {
  let accountDetails = {
    name: "",
    postalCode: "",
    address: "",
    requirements: {

    },
  };

  // if there is no details related to such id, return empty details
  // (TODO)
  return accountDetails;
}

function renderRequirements(requirements) {
  for (let requirement in requirements) {
    
  }
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = HOMEPAGE_PATH;
});

const editButton = document.getElementById('edit');
editButton.addEventListener('click', (_) => {

});
