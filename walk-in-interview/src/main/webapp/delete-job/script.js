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

const STRINGS = AppStrings["delete-job"];

/**
 * Clicks the button to mark this job post as DELETED.
 * 
 * @param {String} jobId Job id for this job post.
 */
function deleteJobPost(jobId) {
    const deleteButtonElement = document.getElementById("delete");

    deleteButtonElement.innerText = STRINGS["delete"];
    deleteButtonElement.addEventListener("click", () => {
        sendRequest(jobId);
    });
}

/**
 * Tells the server to delete the this job post.
 * 
 * @param {String} jobId Job id for this job post.
 */
function sendRequest(jobId) {
    const params = new URLSearchParams();
    params.append("jobId", jobId);
    fetch("/jobs/delete", {method: "PATCH", body: params});
}