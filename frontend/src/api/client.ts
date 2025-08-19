import axios from 'axios'

class ApiClient {
  private instance = axios.create({ baseURL: '/' })

  setAuthToken(token: string | null) {
    if (token) {
      this.instance.defaults.headers.common['Authorization'] = `Bearer ${token}`
    } else {
      delete this.instance.defaults.headers.common['Authorization']
    }
  }

  async get<T = any>(url: string) {
    const { data } = await this.instance.get<T>(url)
    return data
  }
  async post<T = any>(url: string, body?: any) {
    const { data } = await this.instance.post<T>(url, body)
    return data
  }
  async patch<T = any>(url: string, body?: any) {
    const { data } = await this.instance.patch<T>(url, body)
    return data
  }
  async del<T = any>(url: string) {
    const { data } = await this.instance.delete<T>(url)
    return data
  }
}

export const api = new ApiClient()

