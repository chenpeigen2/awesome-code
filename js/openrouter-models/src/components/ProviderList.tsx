import type { ProviderGroup } from "../types/model";

interface ProviderListProps {
  providers: ProviderGroup[];
  selected: string | null;
  onSelect: (provider: string | null) => void;
}

export function ProviderList({ providers, selected, onSelect }: ProviderListProps) {
  return (
    <aside className="w-56 shrink-0 bg-zinc-900 border-r border-zinc-700 overflow-y-auto py-3 max-md:w-full max-md:max-h-52 max-md:border-r-0 max-md:border-b">
      <h2 className="text-xs uppercase tracking-wider text-zinc-500 px-4 pb-2">Providers</h2>
      <button
        className={`w-full flex items-center justify-between px-4 py-2 text-sm text-zinc-200 hover:bg-zinc-800 transition-colors ${
          selected === null ? "bg-indigo-500/15 text-indigo-400" : ""
        }`}
        onClick={() => onSelect(null)}
      >
        <span className="truncate">All</span>
        <span className={`text-xs px-2 py-0.5 rounded-full ${
          selected === null ? "bg-indigo-500/15 text-indigo-400" : "bg-zinc-800 text-zinc-500"
        }`}>
          {providers.reduce((s, p) => s + p.count, 0)}
        </span>
      </button>
      {providers.map((pg) => (
        <button
          key={pg.provider}
          className={`w-full flex items-center justify-between px-4 py-2 text-sm text-zinc-200 hover:bg-zinc-800 transition-colors ${
            selected === pg.provider ? "bg-indigo-500/15 text-indigo-400" : ""
          }`}
          onClick={() => onSelect(pg.provider)}
        >
          <span className="truncate">{pg.provider}</span>
          <span className={`text-xs px-2 py-0.5 rounded-full ml-2 shrink-0 ${
            selected === pg.provider ? "bg-indigo-500/15 text-indigo-400" : "bg-zinc-800 text-zinc-500"
          }`}>
            {pg.count}
          </span>
        </button>
      ))}
    </aside>
  );
}
