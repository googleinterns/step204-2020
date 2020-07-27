/**
 * This file is specific to rendering and add markers to a specific map.
 * It contains the functions necessary to the google maps api.
 */

// coordinates for centering the map around Singapore
const SG_LATITUDE = 1.3521;
const SG_LONGITUDE = 103.8198;
const SG_MAP_ZOOM = 11;

const JOB_DETAILS_PATH = '/job-details/index.html';

/**
 * Creates a map in the provided html div.
 * @param {String} mapElementId The map element id.
 * @return {google.maps.Map} The map object created.
 */
function createMap(mapElementId) {
  console.log('in here with this id: ', mapElementId);
  if (mapElementId === '') {
    throw new Error('map element id should not be empty');
  }

  return new google.maps.Map(document.getElementById(mapElementId), {
    center: {lat: SG_LATITUDE, lng: SG_LONGITUDE},
    zoom: SG_MAP_ZOOM,
  });
}

/**
 * Drops the markers so that the user can see them falling.
 * The pins indicate the location of the job. You can also
 * click on them to get some information about them.
 * @param {google.maps.Map} map The map to add the marker to.
 * @param {Object} job The job to be added to the marker.
 */
function addMarker(map, job) {
  console.log('in here with this job: ', job);
  console.log('and this map', map);
  if (map === null) {
    throw new Error('map should not be null');
  }

  const jobLocation = job['jobLocation'];
  const newJobMarker = new google.maps.Marker({
    position: {lat: jobLocation['latitude'], lng: jobLocation['longitude']},
    map: map,
    animation: google.maps.Animation.DROP,
    title: job['jobTitle'],
  });
  newJobMarker.addListener('click', function() {
    // TODO(issue/xx): somehow link to the job post in the list
    createInfoWindow(job).open(map, newJobMarker);
  });
}

/**
 * This creates an info window for the marker.
 * @param {Object} job The job that corresponds to the marker.
 * @return {google.maps.InfoWindow} The info window created.
 */
function createInfoWindow(job) {
  const pay = job['jobPay'];
  const payStr = `${pay['min']} - ${pay['max']} SGD ` +
  `(${pay['paymentFrequency'].toLowerCase()})`;

  return new google.maps.InfoWindow({
    content: `<form method="GET" action=${JOB_DETAILS_PATH}>` +
    `<input type="hidden" name="jobId" value=${job['jobId']}` +
    '<div style="font-weight:bold;">' + job['title'] +
    '</div><div style="font-style:italic;">' + payStr + '</div>' +
    `<input type"submit">Show Details</button></form>`,
  });
}

export {createMap, addMarker};
