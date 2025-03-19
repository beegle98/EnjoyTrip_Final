// src/stores/alert.js
import { defineStore } from 'pinia'

export const useAlertStore = defineStore('alert', {
    state: () => ({
        message: '',
        type: 'info', // 'info', 'success', 'warning', 'error'
        show: false,
        action: null
    }),
    actions: {
        showAlert({ message, type = 'info', action = null }) {
            this.message = message
            this.type = type
            this.show = true
            this.action = action
        },
        hideAlert() {
            this.show = false
            this.message = ''
            this.action = null
        }
    }
})