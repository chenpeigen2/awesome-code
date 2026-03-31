import { useState, useCallback, type CSSProperties } from 'react';
import { useMatrix } from '../contexts/MatrixContext';
import { playKeySound, playInjectSound, playClickSound } from '../utils/sound';
import type { PoolKey } from '../utils/chars';

export default function ControlPanel() {
  const { state, dispatch, MODES, THEMES } = useMatrix();
  const [inputText, setInputText] = useState(state.userText);
  const [showSettings, setShowSettings] = useState(false);

  const handleInject = useCallback(() => {
    if (!inputText.trim()) return;
    dispatch({ type: 'SET_USER_TEXT', payload: inputText.trim() });
    if (state.soundEnabled) playInjectSound();
  }, [inputText, dispatch, state.soundEnabled]);

  const handleKeyDown = useCallback((e: React.KeyboardEvent<HTMLInputElement>) => {
    if (state.soundEnabled) playKeySound();
    if (e.key === 'Enter') handleInject();
  }, [handleInject, state.soundEnabled]);

  return (
    <div style={styles.wrapper}>
      {/* Mode selector */}
      <div style={styles.row}>
        <span style={styles.label}>模式</span>
        <div style={styles.btnGroup}>
          {MODES.map(m => (
            <button
              key={m.id}
              style={{
                ...styles.modeBtn,
                ...(state.mode === m.id ? styles.modeBtnActive : {}),
              }}
              onClick={() => {
                dispatch({ type: 'SET_MODE', payload: m.id });
                if (state.soundEnabled) playClickSound();
              }}
            >
              {m.label}
            </button>
          ))}
        </div>
      </div>

      {/* Theme selector */}
      <div style={styles.row}>
        <span style={styles.label}>主题</span>
        <div style={styles.btnGroup}>
          {THEMES.map(t => (
            <button
              key={t.id}
              style={{
                ...styles.themeBtn,
                background: state.theme === t.id ? t.primary + '40' : 'rgba(255,255,255,0.05)',
                borderColor: state.theme === t.id ? t.primary : 'rgba(255,255,255,0.15)',
              }}
              onClick={() => {
                dispatch({ type: 'SET_THEME', payload: t.id });
                if (state.soundEnabled) playClickSound();
              }}
            >
              <span style={{ ...styles.colorDot, background: t.primary }} />
              {t.label}
            </button>
          ))}
        </div>
      </div>

      {/* Text input */}
      <div style={styles.row}>
        <span style={styles.label}>注入</span>
        <div style={styles.inputGroup}>
          <input
            style={styles.input}
            value={inputText}
            onChange={e => setInputText(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="输入文字注入矩阵..."
          />
          <button style={styles.injectBtn} onClick={handleInject}>[ 注入 ]</button>
        </div>
      </div>

      {/* Settings toggle */}
      <button
        style={styles.settingsToggle}
        onClick={() => setShowSettings(s => !s)}
      >
        {showSettings ? '▽ 高级设置' : '▷ 高级设置'}
      </button>

      {showSettings && (
        <div style={styles.settings}>
          <SettingToggle label="音效" value={state.soundEnabled} onChange={() => dispatch({ type: 'TOGGLE_SOUND' })} />
          <SettingToggle label="暂停" value={state.paused} onChange={() => dispatch({ type: 'TOGGLE_PAUSED' })} />
          <SettingToggle label="故障特效" value={state.glitchEnabled} onChange={() => dispatch({ type: 'TOGGLE_GLITCH' })} />
          <SettingToggle label="扫描线" value={state.scanlinesEnabled} onChange={() => dispatch({ type: 'TOGGLE_SCANLINES' })} />
          <SettingToggle label="统计" value={state.showStats} onChange={() => dispatch({ type: 'TOGGLE_STATS' })} />

          <div style={styles.sliderRow}>
            <span style={styles.sliderLabel}>字号: {state.fontSize}px</span>
            <input
              type="range" min={12} max={32} value={state.fontSize}
              onChange={e => dispatch({ type: 'SET_FONT_SIZE', payload: +e.target.value })}
              style={styles.slider}
            />
          </div>

          <div style={styles.sliderRow}>
            <span style={styles.sliderLabel}>密度: {Math.round(state.density * 100)}%</span>
            <input
              type="range" min={20} max={200} value={Math.round(state.density * 100)}
              onChange={e => dispatch({ type: 'SET_DENSITY', payload: +e.target.value / 100 })}
              style={styles.slider}
            />
          </div>

          <CharPoolSelector pools={state.charPools} onChange={p => dispatch({ type: 'SET_CHAR_POOLS', payload: p })} />
        </div>
      )}
    </div>
  );
}

// ========== Sub-components ==========

function SettingToggle({ label, value, onChange }: {
  label: string;
  value: boolean;
  onChange: () => void;
}) {
  return (
    <div style={styles.toggleRow} onClick={onChange}>
      <span style={styles.toggleLabel}>{label}</span>
      <span style={{ ...styles.toggleIndicator, color: value ? '#00ff41' : '#666' }}>
        {value ? 'ON' : 'OFF'}
      </span>
    </div>
  );
}

const ALL_POOL_OPTIONS: { id: PoolKey; label: string }[] = [
  { id: 'katakana', label: '片假名' },
  { id: 'digits', label: '数字' },
  { id: 'symbols', label: '符号' },
  { id: 'latin', label: '拉丁' },
  { id: 'chinese', label: '汉字' },
];

function CharPoolSelector({ pools, onChange }: {
  pools: PoolKey[];
  onChange: (p: PoolKey[]) => void;
}) {
  const toggle = (id: PoolKey) => {
    if (pools.includes(id)) {
      if (pools.length > 1) onChange(pools.filter(p => p !== id));
    } else {
      onChange([...pools, id]);
    }
  };

  return (
    <div style={styles.poolRow}>
      <span style={styles.sliderLabel}>字符集:</span>
      <div style={styles.poolBtns}>
        {ALL_POOL_OPTIONS.map(p => (
          <button
            key={p.id}
            style={{
              ...styles.poolBtn,
              opacity: pools.includes(p.id) ? 1 : 0.3,
            }}
            onClick={() => toggle(p.id)}
          >
            {p.label}
          </button>
        ))}
      </div>
    </div>
  );
}

// ========== Styles ==========

const styles: Record<string, CSSProperties> = {
  wrapper: {
    position: 'fixed',
    bottom: 20,
    left: '50%',
    transform: 'translateX(-50%)',
    zIndex: 20,
    display: 'flex',
    flexDirection: 'column',
    gap: 8,
    background: 'rgba(0,0,0,0.75)',
    backdropFilter: 'blur(12px)',
    border: '1px solid rgba(255,255,255,0.1)',
    borderRadius: 12,
    padding: '14px 18px',
    minWidth: 420,
    maxWidth: '90vw',
    fontFamily: '"PingFang SC","Microsoft YaHei",monospace',
  },
  row: {
    display: 'flex',
    alignItems: 'center',
    gap: 10,
  },
  label: {
    color: 'rgba(255,255,255,0.4)',
    fontSize: 12,
    minWidth: 32,
    flexShrink: 0,
  },
  btnGroup: {
    display: 'flex',
    gap: 4,
    flexWrap: 'wrap',
  },
  modeBtn: {
    padding: '4px 10px',
    border: '1px solid rgba(255,255,255,0.15)',
    background: 'rgba(255,255,255,0.03)',
    color: 'rgba(255,255,255,0.6)',
    borderRadius: 4,
    cursor: 'pointer',
    fontSize: 12,
    fontFamily: 'monospace',
    transition: 'all 0.2s',
  },
  modeBtnActive: {
    background: 'rgba(0,255,65,0.15)',
    borderColor: 'rgba(0,255,65,0.5)',
    color: '#00ff41',
    textShadow: '0 0 8px rgba(0,255,65,0.5)',
  },
  themeBtn: {
    padding: '4px 8px',
    border: '1px solid rgba(255,255,255,0.15)',
    borderRadius: 4,
    cursor: 'pointer',
    fontSize: 11,
    fontFamily: 'monospace',
    color: '#ccc',
    display: 'flex',
    alignItems: 'center',
    gap: 4,
    transition: 'all 0.2s',
  },
  colorDot: {
    display: 'inline-block',
    width: 8,
    height: 8,
    borderRadius: '50%',
  },
  inputGroup: {
    display: 'flex',
    gap: 6,
    flex: 1,
  },
  input: {
    flex: 1,
    padding: '6px 12px',
    border: '1px solid rgba(0,255,65,0.2)',
    background: 'rgba(0,0,0,0.5)',
    color: '#00ff41',
    borderRadius: 4,
    fontSize: 13,
    fontFamily: 'monospace',
    outline: 'none',
    minWidth: 0,
  },
  injectBtn: {
    padding: '6px 14px',
    border: '1px solid rgba(0,255,65,0.3)',
    background: 'rgba(0,255,65,0.08)',
    color: '#00ff41',
    borderRadius: 4,
    cursor: 'pointer',
    fontSize: 13,
    fontFamily: 'monospace',
    whiteSpace: 'nowrap',
  },
  settingsToggle: {
    background: 'none',
    border: 'none',
    color: 'rgba(255,255,255,0.3)',
    cursor: 'pointer',
    fontSize: 11,
    fontFamily: 'monospace',
    padding: '4px 0',
    textAlign: 'left',
  },
  settings: {
    display: 'flex',
    flexDirection: 'column',
    gap: 6,
    paddingTop: 6,
    borderTop: '1px solid rgba(255,255,255,0.08)',
  },
  toggleRow: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    cursor: 'pointer',
    padding: '2px 0',
  },
  toggleLabel: {
    color: 'rgba(255,255,255,0.5)',
    fontSize: 12,
  },
  toggleIndicator: {
    fontSize: 12,
    fontFamily: 'monospace',
    fontWeight: 'bold',
  },
  sliderRow: {
    display: 'flex',
    alignItems: 'center',
    gap: 8,
  },
  sliderLabel: {
    color: 'rgba(255,255,255,0.5)',
    fontSize: 11,
    minWidth: 70,
  },
  slider: {
    flex: 1,
    accentColor: '#00ff41',
    height: 4,
  },
  poolRow: {
    display: 'flex',
    alignItems: 'center',
    gap: 8,
  },
  poolBtns: {
    display: 'flex',
    gap: 3,
    flexWrap: 'wrap',
  },
  poolBtn: {
    padding: '2px 6px',
    border: '1px solid rgba(255,255,255,0.1)',
    background: 'rgba(255,255,255,0.03)',
    color: '#aaa',
    borderRadius: 3,
    cursor: 'pointer',
    fontSize: 10,
    fontFamily: 'monospace',
    transition: 'opacity 0.2s',
  },
};
