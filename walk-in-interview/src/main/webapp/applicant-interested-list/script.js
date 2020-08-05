/**
 * This file is specific to interested-list.html
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../strings.en.js';
import {StringsFormat} from '../strings.format.en.js';
import {JOB_ID_PARAM, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_INDEX,
    setErrorMessage, getRequirementsList} from '../common-functions.js';
import {createMap, addMarker} from '../maps.js';
import {API} from '../apis.js';

const STRINGS = AppStrings['applicant-interested-list'];
const JOB_STRINGS = AppStrings['job'];
const JOB_DETAILS_PATH = '../job-details/index.html';
const HOMEPAGE_PATH = '../index.html';

// TODO(issue/34): implement pagination for job listings

let map;

window.onload = () => {
  loadAndDisplayInterestedJobListings();
}

/**
 * Add the list of interested jobs.
 */
async function loadAndDisplayInterestedJobListings() {
  map = createMap('interested-jobs-map');

  const backButton = document.getElementById('back');
  backButton.innerText = STRINGS['back'];
  backButton.addEventListener('click', (_) => {
    window.location.href = HOMEPAGE_PATH;
  });

  const jobListingsTitle =
    document.getElementById('job-listings-title');
  jobListingsTitle.innerText = STRINGS['job-listings-title'];

  const jobPageData = 
    await getInterestedJobs(DEFAULT_PAGE_SIZE, DEFAULT_PAGE_INDEX)
      .catch((error) => {
        console.error('error fetching job listings', error);
        setErrorMessage(/* errorMessageElementId= */ 'error-message',
            /* msg= */ STRINGS['get-jobs-error-message'],
            /* include default msg= */ false);
      });
  
  displayInterestedJobListings(jobPageData);
}

/**
 * Makes GET request to retrieve all the interested job listings
 * belonging to the current applicant user. 
 * This function is called when the interest page is loaded.
 * 
 * @param {int} pageSize The number of jobs for one page.
 * @param {int} pageIndex The page index (starting from 0).
 * @return {Object} The data returned from the servlet.
 */
function getInterestedJobs(pageSize, pageIndex) {
  const params = `pageSize=${pageSize}&pageIndex=${pageIndex}`;
    
  fetch(`${API['applicant-interested-list']}?${params}`, {
    method: 'GET',
    headers: {'Content-Type': 'application/json'},
  })
      .then((response) => response.json())
      .then((data) => {
        console.log('data', data);
        // Resets the error (there might have been an error msg from earlier)
        setErrorMessage(/* errorMessageElementId= */ 'error-message',
            /* msg= */ '', /* includesDefaultMsg= */ false);
        return data;
      });
}

/**
 * This will add all the interested job listings onto the page.
 * 
 * @param {Object} jobPageData The details to be shown on the page.
 */
function displayInterestedJobListings(jobPageData) {
  const jobListingsElement = document.getElementById('job-listings');
  const jobShowing = document.getElementById('job-listings-showing');

  /* reset the list so we don't render the same jobs twice */
  jobListingsElement.innerHTML = '';
  jobShowing.innerHTML = '';

  if (jobPageData === undefined ||
    !jobPageData.hasOwnProperty('jobList') ||
    jobPageData['jobList'].length === 0) {
    setErrorMessage(/* errorMessageElementId= */ 'error-message',
        /* msg= */ STRINGS['no-jobs-error-message'],
        /* includesDefaultMsg= */ false);
    return;
  }

  const jobListings = jobPageData['jobList'];

  jobListings.forEach((job) => {
    jobListingsElement.appendChild(buildJobElement(job));
  });

  jobShowing.innerText = StringsFormat['jobShowing']
    .replace('{MINIMUM}', jobPageData['range'].minimum)
    .replace('{MAXIMUM}', jobPageData['range'].maximum)
    .replace('{JOB_LISTINGS_SHOWING}', STRINGS['job-listings-showing'])
    .replace('{TOTAL_COUNT}', jobPageData['totalCount']);
}

/**
 * Builds the job element given the job details from the servlet response.
 * 
 * @param {Object} job The job to be displayed.
 * @return {Element} The job listing element.
 */
function buildJobElement(job) {
  const jobPostPreviewTemplate =
    document.getElementById('job-listing-template');

  const jobPostPreview =
    jobPostPreviewTemplate.cloneNode( /* and child elements */ true);
  jobPostPreview.setAttribute('id', 'job-listing-id-' + job['jobId']);

  const jobTitle = jobPostPreview.children[0];
  jobTitle.innerText = job['jobTitle'];

  const jobAddress = jobPostPreview.children[1];
  const location = job['jobLocation'];
  jobAddress.innerText = StringsFormat['jobAddressDescription']
    .replace('{ADDRESS}', location['address'])
    .replace('{POSTAL_CODE}', location['postalCode']);

  const jobPay = jobPostPreview.children[2];
  const pay = job['jobPay'];
  jobPay.innerText = StringsFormat['jobPayDescription']
    .replace('{MIN_PAY}', pay['min'])
    .replace('{MAX_PAY}', pay['max'])
    .replace('{CURRENCY}', JOB_STRINGS['sgd'])
    .replace('{FREQUENCY}', JOB_STRINGS['pay-frequency'][pay['paymentFrequency']]);

  const requirementsList = jobPostPreview.children[3];
  const fullRequirementsList = getRequirementsList();
  const requirementsArr = [];

  const jobRequirements = job['requirements'];
  for (const key in jobRequirements) {
    if (jobRequirements.hasOwnProperty(key)) {
      if (jobRequirements[key] /* is true */) {
        requirementsArr.push(fullRequirementsList[key]);
      }
    }
  }

  requirementsList.innerText = StringsFormat['requirementsDescription']
    .replace('{REQUIREMENTS_TITLE}', JOB_STRINGS['requirements-title'])
    .replace('{REQUIREMENTS_LIST}', requirementsArr.join(', '));

  const detailsForm = jobPostPreview.children[4];
  detailsForm.method = 'GET';
  detailsForm.action = JOB_DETAILS_PATH;

  const jobIdElement = jobPostPreview.children[4].children[0];
  jobIdElement.setAttribute('type', 'hidden');
  jobIdElement.setAttribute('name', JOB_ID_PARAM);
  const jobId = job[JOB_ID_PARAM];
  jobIdElement.setAttribute('value', jobId);

  jobPostPreview.addEventListener('click', (_) => {
    if (jobId === '') {
      throw new Error('jobId should not be empty');
    }
    detailsForm.submit();
  });

  const marker = addMarker(map, job);
  marker.addListener('click', function() {
    new google.maps.InfoWindow({
      content: jobPostPreview.innerHTML,
    }).open(map, marker);
    // TODO(issue/73): link this to the job in the list?
  });

  // double clicking on the marker goes to the job details page
  marker.addListener('dblclick', function() {
    if (jobId === '') {
      throw new Error('jobId should not be empty');
    }
    detailsForm.submit();
  });

  return jobPostPreview;
}
