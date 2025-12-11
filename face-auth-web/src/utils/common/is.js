export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

export function isNull(val) {
  return val === null
}

export function isUndef(val) {
  return typeof val === 'undefined'
}

export function isNullOrUndef(val) {
  return isNull(val) || isUndef(val)
}
