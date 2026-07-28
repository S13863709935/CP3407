const test = require('node:test')
const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')

const root = path.resolve(__dirname, '..')

function read(relativePath) {
  return fs.readFileSync(path.join(root, relativePath), 'utf8')
}

test('router exposes the delivered resident journeys', () => {
  const router = read('src/router/index.js')
  const requiredRoutes = [
    '/login',
    '/register',
    "path: 'home'",
    "path: 'goodsDetail'",
    "path: 'collect'",
    "path: 'notice'",
    "path: 'addGoods'",
    "path: 'goods'",
    "path: 'orders'",
    "path: 'feedback'",
    "path: 'userFeedback'",
    "path: 'search'"
  ]

  requiredRoutes.forEach(route => {
    assert.match(router, new RegExp(route.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')))
  })
})

test('listing form submits canonical publication values', () => {
  const addGoods = read('src/views/front/AddGoods.vue')
  assert.match(addGoods, /listingStatus\.LISTED/)
  assert.match(addGoods, /listingStatus\.OFF_SHELF/)
  assert.match(addGoods, /normaliseListingStatus/)
})

test('empty search results remain a valid list instead of crashing the page', () => {
  const search = read('src/views/front/Search.vue')
  assert.match(search, /res\.data\?\.list \|\| \[\]/)
  assert.match(search, /res\.data\?\.total \|\| 0/)
})

test('order view exposes buyer and seller tracking modes', () => {
  const orders = read('src/views/front/Orders.vue')
  assert.match(orders, /My Purchases/)
  assert.match(orders, /My Sales/)
  assert.match(orders, /selectSalePage/)
})

test('feedback views expose submission and reply history', () => {
  const feedback = read('src/views/front/Feedback.vue')
  const history = read('src/views/front/UserFeedback.vue')
  assert.match(feedback, /feedback\/add/)
  assert.match(history, /feedback\/selectPage/)
  assert.match(history, /reply/)
})
