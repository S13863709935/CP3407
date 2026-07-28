const LISTING_STATUS = Object.freeze({
  LISTED: 'Listed',
  OFF_SHELF: 'Off-shelf'
})

function normaliseListingStatus(value) {
  if (value === null || value === undefined || value === '') {
    return LISTING_STATUS.OFF_SHELF
  }
  const candidate = String(value).trim().toLowerCase()
  if (['listed', 'on-shelf', 'on shelf', '上架'].includes(candidate)) {
    return LISTING_STATUS.LISTED
  }
  if (['off-shelf', 'off shelf', 'unlisted', '下架'].includes(candidate)) {
    return LISTING_STATUS.OFF_SHELF
  }
  throw new Error(`Unsupported listing status: ${value}`)
}

function listingStatusLabel(value) {
  return normaliseListingStatus(value) === LISTING_STATUS.LISTED
    ? 'On-shelf'
    : 'Off-shelf'
}

module.exports = {
  LISTING_STATUS,
  listingStatusLabel,
  normaliseListingStatus
}
