import type { ProviderGroup } from "../types/model";

interface ProviderListProps {
  providers: ProviderGroup[];
  selected: string | null;
  onSelect: (provider: string | null) => void;
}

export function ProviderList({ providers, selected, onSelect }: ProviderListProps) {
  return (
    <aside className="provider-list">
      <h2>Providers</h2>
      <button
        className={`provider-btn ${selected === null ? "active" : ""}`}
        onClick={() => onSelect(null)}
      >
        <span className="provider-name">All</span>
        <span className="provider-count">{providers.reduce((s, p) => s + p.count, 0)}</span>
      </button>
      {providers.map((pg) => (
        <button
          key={pg.provider}
          className={`provider-btn ${selected === pg.provider ? "active" : ""}`}
          onClick={() => onSelect(pg.provider)}
        >
          <span className="provider-name">{pg.provider}</span>
          <span className="provider-count">{pg.count}</span>
        </button>
      ))}
    </aside>
  );
}
