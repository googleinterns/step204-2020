/**
 * This file is specific to index.html for job-details. 
 * It renders the fields on the page dynamically,
 * and send PATCH request to mark the current job post as deleted.
 */

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from "../strings.en.js";

import {setErrorMessage} from "../common-functions.js";

const STRINGS = AppStrings["delete-job"];
const HOMEPAGE_PATH = "../job-details/index.html";
const RESPONSE_ERROR = "There was an error while deleting the job post. Please try again";

window.onload = () => {
    renderDeleteButton();
};

/**
 * Clicks the button to mark this job post as DELETED.
 */
function renderDeleteButton() {
    const deleteButtonElement = document.getElementById("delete");
    deleteButtonElement.innerText = STRINGS["delete"];
}

/**
 * Gets the job id from the url.
 */
function getJobId() {
    // TODO(issue/53): gets the job Id
    return "";
}

/**
 * Tells the server to delete the this job post.
 * 
 * @param {String} jobId Job id for this job post.
 */
function deleteJobPost(jobId) {
    const params = new URLSearchParams();
    params.append("jobId", jobId);
    fetch("/jobs/delete", {method: "PATCH", body: params})
    .then((response) => response.text())
    .then((data) => {
      console.log('data', data);
      /** reset the error (there might have been an error msg from earlier) */
      setErrorMessage(/* errorMessageElementId= */'error-message', /* msg= */ '', /* includesDefault= */false);
      window.location.href= HOMEPAGE_PATH;
    })
    .catch((error) => {
      setErrorMessage(/* errorMessageElementId= */'error-message', /* msg= */ RESPONSE_ERROR, /* includesDefault= */false);
      console.log('error', error);
    });
}

const deleteButtonElement = document.getElementById("delete");
deleteButtonElement.addEventListener("click", () => {
    const jobId = getJobId();
    if (jobId === "") {
        setErrorMessage(/* errorMessageElementId= */"error-message", /* msg= */ "Empty Job Id found. " + RESPONSE_ERROR,
            /* includesDefault= */false);
        return false;
    }

    deleteJobPost(getJobId());
});
