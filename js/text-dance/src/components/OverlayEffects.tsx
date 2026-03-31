import { useMatrix } from '../contexts/MatrixContext';

export default function OverlayEffects() {
  const { state } = useMatrix();

  return (
    <>
      {/* Vignette */}
      <div style={{
        position: 'fixed',
        top: 0, left: 0, right: 0, bottom: 0,
        pointerEvents: 'none',
        zIndex: 5,
        background: 'radial-gradient(ellipse at center, transparent 40%, rgba(0,0,0,0.6) 100%)',
      }} />

      {/* Scanlines */}
      {state.scanlinesEnabled && (
        <div style={{
          position: 'fixed',
          top: 0, left: 0, right: 0, bottom: 0,
          pointerEvents: 'none',
          zIndex: 6,
          background: `repeating-linear-gradient(
            0deg,
            transparent,
            transparent 2px,
            rgba(0, 0, 0, 0.06) 2px,
            rgba(0, 0, 0, 0.06) 4px
          )`,
        }} />
      )}
    </>
  );
}
