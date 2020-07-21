/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {setCookie, setErrorMessage} from '../common-functions.js';

const UPDATE_JOB_PATH = '../update-job/index.html';

/**
 * Gets job Id of the current job post.
 */
// TODO(issue/53): implement this function
function getJobId() {
  return 'xxxxx';
}

const submitButton = document.getElementById('update');
submitButton.addEventListener('click', (_) => {

  const jobId = getJobId();

  if (jobId === '') {
    setErrorMessage(/* errorMessageElementId= */'error-message', /* msg= */ 'Empty Job Id found.',
      /* includesDefault= */false);
    return;
  }

  setCookie('jobId', jobId);

  fetch('../update-job/index.html', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    credentials: 'include',
  })
  .then(() => {
      /** reset the error (there might have been an error msg from earlier) */
      setErrorMessage(/* errorMessageElementId= */'error-message', /* msg= */ '', /* includesDefault= */false);
      window.location.href= UPDATE_JOB_PATH;
  })
  .catch((error) => {
      setErrorMessage(/* errorMessageElementId= */'error-message', /* msg= */ 'Error occur when sending jobId',
          /* includesDefault= */false);
      console.log('error', error);
  });
});
