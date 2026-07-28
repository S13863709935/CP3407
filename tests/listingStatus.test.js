const test = require('node:test')
const assert = require('node:assert/strict')

const {
  LISTING_STATUS,
  listingStatusLabel,
  normaliseListingStatus
} = require('../src/utils/listingStatus')

test('uses stable values shared with the backend and database', () => {
  assert.equal(LISTING_STATUS.LISTED, 'Listed')
  assert.equal(LISTING_STATUS.OFF_SHELF, 'Off-shelf')
})

test('normalises legacy on-shelf values', () => {
  assert.equal(normaliseListingStatus('On-shelf'), LISTING_STATUS.LISTED)
  assert.equal(normaliseListingStatus('上架'), LISTING_STATUS.LISTED)
  assert.equal(listingStatusLabel('Listed'), 'On-shelf')
})

test('normalises legacy off-shelf values and safe empty defaults', () => {
  assert.equal(normaliseListingStatus('Unlisted'), LISTING_STATUS.OFF_SHELF)
  assert.equal(normaliseListingStatus('下架'), LISTING_STATUS.OFF_SHELF)
  assert.equal(normaliseListingStatus(null), LISTING_STATUS.OFF_SHELF)
})

test('rejects unsupported publication states', () => {
  assert.throws(() => normaliseListingStatus('Reserved'), /Unsupported listing status/)
})
