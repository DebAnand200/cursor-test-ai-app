import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuthStore } from '../state/authStore'

export const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const token = useAuthStore((s) => s.token)
  if (!token) return <Navigate to="/login" replace />
  return <>{children}</>
}

