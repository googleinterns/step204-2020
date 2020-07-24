/**
 * This file is specific to rendering and add markers to a specific map.
 */

import {SG_LATITUDE, SG_LONGITUDE, SG_MAP_ZOOM} from './common-functions.js';

const jobMarkers = [];

/**
 * Creates a map in the provided html div.
 * @param {String} mapElementId The map element id.
 * @return {google.maps.Map} The map object created.
 */
function createMap(mapElementId) {
  if (mapElementId === '') {
    throw new Error('map element id should not be empty');
  }

  return new google.maps.Map(document.getElementById(mapElementId), {
    center: {lat: SG_LATITUDE, lng: SG_LONGITUDE},
    zoom: SG_MAP_ZOOM,
  });
}

// /**
//  * Add markers for the provided array of jobs.
//  * The "i * 200" essentially means that each marker will be placed 200ms
//  * after each other. It's a good value because it is short enough that the
//  * user won't be waiting too long for the pins to drop, but also long enough
//  * that the user can see the effect of the pins dropping individually.
//  * @param {google.maps.Map} map The map to add the markers to.
//  * @param {Array} jobList The job markers to be added to the map.
//  * @return {Array} Array of all the job markers created.
//  */
// function showAllMarkers(map, jobList) {
//   clearAllMarkers();
//   for (let i = 0; i < jobList.length; i++) {
//     window.setTimeout(function() {
//       addMarker(map, jobList[i]);
//     }, i * 200);
//   }
//   return [];
// }

/**
 * Drops the markers so that the user can see them falling.
 * The pins indicate the location of the job. You can also
 * click on them to get some information about them.
 * @param {google.maps.Map} map The map to add the marker to.
 * @param {Object} job The job to be added to the marker.
 * @return {google.maps.Marker} The marker created.
 */
function addMarker(map, job) {
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
  newMarker.addListener('click', function() {
    // TODO(issue/xx): somehow link to the job post in the list
  });
  return newJobMarker;
}

// /** clear all the markers */
// function clearAllMarkers() {
//   for (let i = 0; i < jobMarkers.length; i++) {
//     jobMarkers[i].setMap(null);
//   }
//   jobMarkers = [];
// }

export {createMap, addMarker};
