import js from '@eslint/js';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import tseslint from 'typescript-eslint';
import prettier from 'eslint-config-prettier';

export default tseslint.config(
  { ignores: ['.vite', 'dist', 'coverage', 'playwright-report'] },
  js.configs.recommended,
  ...tseslint.configs.recommended,
  {
    files: ['**/*.{ts,tsx}'],
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh
    },
    languageOptions: {
      ecmaVersion: 2020,
      globals: {
        window: 'readonly',
        document: 'readonly',
        navigator: 'readonly',
        Blob: 'readonly',
        URL: 'readonly',
        localStorage: 'readonly',
        HTMLVideoElement: 'readonly',
        HTMLInputElement: 'readonly',
        Response: 'readonly'
      }
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': ['warn', { allowConstantExport: true }]
    }
  },
  {
    files: ['**/*.cjs'],
    languageOptions: {
      globals: {
        module: 'readonly',
        require: 'readonly'
      }
    }
  },
  prettier
);
