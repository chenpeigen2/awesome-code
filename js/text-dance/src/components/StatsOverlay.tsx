import { useMatrix } from '../contexts/MatrixContext';

export default function StatsOverlay() {
  const { state, themeConfig, stats } = useMatrix();
  const theme = themeConfig();

  if (!state.showStats) return null;

  return (
    <div style={{
      position: 'fixed',
      top: 14,
      right: 18,
      color: theme.primary,
      fontFamily: 'monospace',
      fontSize: 11,
      opacity: 0.5,
      textAlign: 'right',
      zIndex: 10,
      pointerEvents: 'none',
      lineHeight: 1.6,
    }}>
      <div>FPS: {stats.fps}</div>
      <div>COLUMNS: {stats.columns}</div>
      <div>CHARS: {stats.chars}</div>
      <div>MODE: {state.mode.toUpperCase()}</div>
    </div>
  );
}
