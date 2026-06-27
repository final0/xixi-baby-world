import request from './request'

export const chat = (message, sessionId) => request.post('/ai/chat', { message, sessionId })
export const clearSession = (sessionId) => request.delete(`/ai/session/${sessionId}`)
