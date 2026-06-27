import request from './request'

export const getCaptcha = () => request.get('/auth/captcha')
export const sendEmailCode = (email) => request.post('/auth/send-email-code', { email })
export const register = (data) => request.post('/auth/register', data)
export const login = (data) => request.post('/auth/login', data)
export const logout = () => request.post('/auth/logout')
