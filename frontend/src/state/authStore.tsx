import React, { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { jwtDecode } from 'jwt-decode'
import { api } from '../api/client'

type Decoded = { sub: string; tier?: string; exp: number }

type AuthState = {
  token: string | null
  email: string | null
  tier: 'FREE' | 'PRO' | null
  setToken: (token: string | null) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      email: null,
      tier: null,
      setToken: (token) => {
        if (!token) {
          set({ token: null, email: null, tier: null })
          return
        }
        try {
          const decoded = jwtDecode<Decoded>(token)
          set({ token, email: decoded.sub, tier: (decoded.tier as any) ?? 'FREE' })
        } catch {
          set({ token: null, email: null, tier: null })
        }
      },
      logout: () => set({ token: null, email: null, tier: null }),
    }),
    { name: 'auth' }
  )
)

type AuthContextType = {
  login: (email: string, password: string) => Promise<void>
  signup: (email: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { token, setToken, logout } = useAuthStore()
  const [ready, setReady] = useState(false)

  useEffect(() => {
    setReady(true)
  }, [])

  useEffect(() => {
    api.setAuthToken(token)
  }, [token])

  const value = useMemo<AuthContextType>(() => ({
    async login(email, password) {
      const res = await api.post('/api/auth/login', { email, password })
      setToken(res.token)
    },
    async signup(email, password) {
      const res = await api.post('/api/auth/signup', { email, password })
      setToken(res.token)
    },
    logout,
  }), [setToken, logout])

  if (!ready) return null
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

