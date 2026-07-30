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

test('item details guard order creation and expose seller communication', () => {
  const details = read('src/views/front/GoodsDetail.vue')
  assert.match(details, /chatGroup\/add/)
  assert.match(details, /if \(!this\.form\.addressId\)/)
  assert.match(details, /orders\/add/)
  assert.match(details, /this\.addressList = res\.data \|\| \[\]/)
  assert.match(details, /this\.goods = res\.data \|\| \{\}/)
})

test('resident list views preserve stable empty states', () => {
  const listViews = [
    'src/views/front/Collect.vue',
    'src/views/front/Orders.vue',
    'src/views/front/UserFeedback.vue'
  ]

  listViews.forEach(file => {
    const source = read(file)
    assert.match(source, /res\.data\?\.list \|\| \[\]/)
    assert.match(source, /res\.data\?\.total \|\| 0/)
  })

  const notices = read('src/views/front/Notice.vue')
  assert.match(notices, /this\.noticeList = res\.data \|\| \[\]/)
})

test('feedback requires a subject and content before submission', () => {
  const feedback = read('src/views/front/Feedback.vue')
  assert.match(feedback, /title:\s*\[\s*\{\s*required: true/)
  assert.match(feedback, /content:\s*\[\s*\{\s*required: true/)
  assert.match(feedback, /this\.\$refs\.formRef\.validate/)
})

test('profile flow persists identity changes in the browser session', () => {
  const profile = read('src/views/front/Person.vue')
  assert.match(profile, /user\/update/)
  assert.match(profile, /localStorage\.setItem\('xm-user'/)
  assert.match(profile, /\$emit\('update:user'\)/)
})

test('order actions are restricted in the interface by actor and status', () => {
  const orders = read('src/views/front/Orders.vue')
  assert.match(orders, /scope\.row\.userId === user\.id/)
  assert.match(orders, /scope\.row\.saleId === user\.id/)
  assert.match(orders, /changeStatus\(scope\.row, '待收货'\)/)
  assert.match(orders, /changeStatus\(scope\.row, '已完成'\)/)
})
