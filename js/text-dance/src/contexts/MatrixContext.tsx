import { createContext, useContext, useReducer, useCallback, useState, type ReactNode, type Dispatch } from 'react';
import type { MatrixState, MatrixAction, ModeConfig, Theme, Stats, MatrixContextValue } from '../types';

const MatrixContext = createContext<MatrixContextValue | null>(null);

export const MODES: ModeConfig[] = [
  { id: 'classic', label: '经典', speed: [1.5, 4], brightness: [0.4, 0.6], length: [8, 25], density: 1 },
  { id: 'dense', label: '密集', speed: [3, 6], brightness: [0.6, 0.4], length: [15, 35], density: 1 },
  { id: 'sparse', label: '稀疏', speed: [1, 2.5], brightness: [0.3, 0.3], length: [5, 12], density: 0.6 },
  { id: 'turbo', label: '极速', speed: [6, 12], brightness: [0.7, 0.3], length: [10, 30], density: 1 },
  { id: 'chaos', label: '混沌', speed: [1, 10], brightness: [0.2, 0.8], length: [3, 40], density: 1 },
  { id: 'gentle', label: '柔和', speed: [0.5, 1.5], brightness: [0.3, 0.3], length: [6, 15], density: 0.7 },
];

export const THEMES: Theme[] = [
  { id: 'green', label: '经典绿', primary: '#00ff41', secondary: '#00cc33', head: '#ffffff' },
  { id: 'amber', label: '琥珀', primary: '#ffb000', secondary: '#cc8800', head: '#ffffff' },
  { id: 'cyan', label: '赛博青', primary: '#00ffff', secondary: '#00cccc', head: '#ffffff' },
  { id: 'red', label: '红色警报', primary: '#ff0040', secondary: '#cc0033', head: '#ffaaaa' },
  { id: 'purple', label: '暗紫', primary: '#bf00ff', secondary: '#9900cc', head: '#eeccff' },
  { id: 'white', label: '白噪音', primary: '#cccccc', secondary: '#888888', head: '#ffffff' },
];

const initialState: MatrixState = {
  mode: 'classic',
  theme: 'green',
  userText: '觉醒吧 矩阵无所不在',
  fontSize: 18,
  soundEnabled: true,
  showStats: true,
  charPools: ['katakana', 'digits', 'symbols'],
  paused: false,
  glitchEnabled: true,
  scanlinesEnabled: true,
};

function reducer(state: MatrixState, action: MatrixAction): MatrixState {
  switch (action.type) {
    case 'SET_MODE': return { ...state, mode: action.payload };
    case 'SET_THEME': return { ...state, theme: action.payload };
    case 'SET_USER_TEXT': return { ...state, userText: action.payload };
    case 'SET_FONT_SIZE': return { ...state, fontSize: action.payload };
    case 'TOGGLE_SOUND': return { ...state, soundEnabled: !state.soundEnabled };
    case 'TOGGLE_STATS': return { ...state, showStats: !state.showStats };
    case 'TOGGLE_PAUSED': return { ...state, paused: !state.paused };
    case 'TOGGLE_GLITCH': return { ...state, glitchEnabled: !state.glitchEnabled };
    case 'TOGGLE_SCANLINES': return { ...state, scanlinesEnabled: !state.scanlinesEnabled };
    case 'SET_CHAR_POOLS': return { ...state, charPools: action.payload };
    default: return state;
  }
}

export function MatrixProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [stats, setStats] = useState<Stats>({ fps: 0, columns: 0, chars: 0 });

  const modeConfig = useCallback(
    (): ModeConfig => MODES.find(m => m.id === state.mode) ?? MODES[0],
    [state.mode],
  );
  const themeConfig = useCallback(
    (): Theme => THEMES.find(t => t.id === state.theme) ?? THEMES[0],
    [state.theme],
  );

  return (
    <MatrixContext.Provider value={{ state, dispatch, MODES, THEMES, modeConfig, themeConfig, stats, setStats }}>
      {children}
    </MatrixContext.Provider>
  );
}

export function useMatrix(): MatrixContextValue {
  const ctx = useContext(MatrixContext);
  if (!ctx) throw new Error('useMatrix must be used within MatrixProvider');
  return ctx;
}
