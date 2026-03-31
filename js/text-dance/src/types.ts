import type { CSSProperties } from 'react';
import type { PoolKey } from './utils/chars';

// ========== Theme ==========
export interface Theme {
  id: string;
  label: string;
  primary: string;
  secondary: string;
  head: string;
}

// ========== Mode ==========
export interface ModeConfig {
  id: string;
  label: string;
  speed: [number, number];
  brightness: [number, number];
  length: [number, number];
  density: number;
}

// ========== State ==========
export interface MatrixState {
  mode: string;
  theme: string;
  userText: string;
  fontSize: number;
  soundEnabled: boolean;
  showStats: boolean;
  charPools: PoolKey[];
  paused: boolean;
  glitchEnabled: boolean;
  scanlinesEnabled: boolean;
}

export type MatrixAction =
  | { type: 'SET_MODE'; payload: string }
  | { type: 'SET_THEME'; payload: string }
  | { type: 'SET_USER_TEXT'; payload: string }
  | { type: 'SET_FONT_SIZE'; payload: number }
  | { type: 'TOGGLE_SOUND' }
  | { type: 'TOGGLE_STATS' }
  | { type: 'TOGGLE_PAUSED' }
  | { type: 'TOGGLE_GLITCH' }
  | { type: 'TOGGLE_SCANLINES' }
  | { type: 'SET_CHAR_POOLS'; payload: PoolKey[] };

// ========== Stats ==========
export interface Stats {
  fps: number;
  columns: number;
  chars: number;
}

// ========== Context value ==========
export interface MatrixContextValue {
  state: MatrixState;
  dispatch: React.Dispatch<MatrixAction>;
  MODES: ModeConfig[];
  THEMES: Theme[];
  modeConfig: () => ModeConfig;
  themeConfig: () => Theme;
  stats: Stats;
  setStats: (s: Stats) => void;
}

// ========== Entity types ==========
export interface RippleData {
  x: number;
  y: number;
}

export interface GlitchState {
  active: boolean;
  timer: number;
  x: number;
  w: number;
}

export interface MousePos {
  x: number;
  y: number;
}

export interface FpsTracker {
  count: number;
  time: number;
  value: number;
}

// ========== CSS helper ==========
export type Styles = Record<string, CSSProperties>;
