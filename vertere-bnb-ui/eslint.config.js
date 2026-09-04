import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import { defineConfig, globalIgnores } from 'eslint/config'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{js,jsx}'],
    extends: [
      js.configs.recommended,
      reactHooks.configs.flat.recommended,
      reactRefresh.configs.vite,
    ],
    languageOptions: {
      globals: globals.browser,
      parserOptions: { ecmaFeatures: { jsx: true } },
    },
    rules: {
      // This app fetches data in useEffect throughout (the standard
      // "start loading, fetch, stop loading" pattern from React's own
      // docs, each one properly guarded with a `cancelled` flag for
      // cleanup). This rule flags that exact pattern, so it's off here
      // rather than contorting every data-fetching effect to dodge it.
      'react-hooks/set-state-in-effect': 'off',
    },
  },
])
