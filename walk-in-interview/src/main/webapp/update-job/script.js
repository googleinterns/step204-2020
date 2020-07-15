/**
 * This file is specific to update-job.html. It renders the fields on the
 * page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

const CurrentLocale = "en";

import {getJobDetailsFromUserInput, validateRequiredUserInput, setErrorMessage} from "../new-job/script.js";

import {AppStrings} from "./strings.en.js";

const JobIdParam = "jobId";
const HOMEPAGE_PATH = "../job-details/index.html";
const RESPONSE_ERROR = "There was an error while creating" +
  "the job listing, please try submitting again";

window.onload = () => {
    var jobId = getJobId();
    addPageElements(jobId);
};

/**
 * Gets the jobId from job-details.html page.
 */
function getJobId() {
    var jobId = localStorage.getItem(JobIdParam).trim();

    if (jobId === "") {
        // TODO: error handling
    }

    return jobId;
}

/**
 * Adds all the titles to the fields on this page.
 * 
 * @param {String} jobId 
 */
function addPageElements(jobId) {
    const cancelButton = document.getElementById('job-cancel');
    cancelButton.setAttribute("value", "Cancel");
    cancelButton.setAttribute("type", "reset");

    const submitButton = document.getElementById("job-submit");
    submitButton.setAttribute("value","Create");
    submitButton.setAttribute("type", "submit");

    const job = getJobFromId(jobId);

    const jobTitle = document.getElementById("job-title");
    const jobTitleContent = job.jobTitle;
    jobTitle.setAttribute("value", jobTitleContent);

    const jobDescription = document.getElementById("job-description");
    const jobDescriptionContent = job.jobDescription;
    jobDescription.setAttribute("value", jobDescriptionContent);

    const jobAddress = document.getElementById("job-address");
    const jobAddressContent = job.jobLocation.address;
    jobAddress.setAttribute("value", jobAddressContent);
    
    // TODO: requirement list
    //const requirementsTitle = document.getElementById('requirements-title');
    //requirementsTitle.innerText = STRINGS['requirements-title'];
    const requirements = job.requirements;
    renderRequirementsList();

    const jobPayFrequency = document.getElementById("job-pay-frequency");
    // TODO
    
    const jobPayMin = document.getElementById("job-pay-min");
    const jobPayMinContent = job.jobPay.min;
    jobPayMin.setAttribute("value", jobPayMinContent);

    const jobPayMax = document.getElementById("job-pay-max");
    const jobPayMaxContent = job.jobPay.max;
    jobPayMax.setAttribute("value", jobPayMaxContent);

    const jobDuration = document.getElementById("job-duration");
    // TODO

    const jobExpiry = document.getElementById("job-expiry");
    const jobExpiryTimestamp = job.postExpiryTimestamp;
    // Multiplied by 1000 so that the argument is in milliseconds, not seconds.
    var expiryDate = new Date(jobExpiryTimestamp * 1000);
    jobExpiry.setAttribute("value", expiryDate);
}

/**
 * Gets the Job post given its id.
 * 
 * @param {String} jobId Cloud Firestore Job Id.
 * @returns Job json.
 */
function getJobFromId(jobId) {
    const url = "/jobs?jobId=" + jobId;
    fetch(url, {
        method: "GET",
        headers: {'Content-Type': 'application/json'},
    })
    .then(response => response.json())
    .catch(error => {
        // TODO
    })
}

/**
 * Add the list of requirements that are stored in the database.
 * 
 * @param {Array} requirements An array of requirements(string) of the job post.
 */
function renderRequirementsList(requirements) {
    const requirementsListElement = document.getElementById('requirements-list');

    /** reset the list so we don't render the same requirements twice */
  requirementsListElement.innerHTML = '';
  const requirementElementTemplate = document.getElementById('requirement-element-template');
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

const submitButton = document.getElementById('submit');
submitButton.addEventListener('click', (_) => {
  if (!validateRequiredUserInput()) {
    return;
  }

  const jobDetails = getJobDetailsFromUserInput();

  fetch('/jobs', {
    method: 'PATCH',
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
