import type { HFAuthorGroup } from "../types/huggingface";

interface HFAuthorListProps {
  authors: HFAuthorGroup[];
  selected: string | null;
  onSelect: (author: string | null) => void;
  highlightSet?: Set<string>;
}

export function HFAuthorList({ authors, selected, onSelect, highlightSet }: HFAuthorListProps) {
  const totalCount = authors.reduce((s, a) => s + a.count, 0);

  return (
    <aside className="w-56 shrink-0 bg-zinc-900 border-r border-zinc-700 overflow-y-auto py-3 max-md:w-full max-md:max-h-52 max-md:border-r-0 max-md:border-b">
      <h2 className="text-xs uppercase tracking-wider text-zinc-500 px-4 pb-2">
        Authors ({authors.length})
      </h2>
      <button
        className={`w-full flex items-center justify-between px-4 py-2 text-sm text-zinc-200 hover:bg-zinc-800 transition-colors ${
          selected === null ? "bg-yellow-500/15 text-yellow-400" : ""
        }`}
        onClick={() => onSelect(null)}
      >
        <span className="truncate">All</span>
        <span className={`text-xs px-2 py-0.5 rounded-full ${
          selected === null ? "bg-yellow-500/15 text-yellow-400" : "bg-zinc-800 text-zinc-500"
        }`}>
          {totalCount.toLocaleString()}
        </span>
      </button>
      {authors.map((ag) => {
        const hasMatch = highlightSet ? highlightSet.has(ag.author) : true;
        return (
          <button
            key={ag.author}
            className={`w-full flex items-center justify-between px-4 py-2 text-sm transition-colors ${
              selected === ag.author
                ? "bg-yellow-500/15 text-yellow-400"
                : hasMatch
                  ? "text-zinc-200 hover:bg-zinc-800"
                  : "text-zinc-600 hover:bg-zinc-800"
            }`}
            onClick={() => onSelect(ag.author)}
          >
            <span className="truncate">{ag.author}</span>
            <span className={`text-xs px-2 py-0.5 rounded-full ml-2 shrink-0 ${
              selected === ag.author
                ? "bg-yellow-500/15 text-yellow-400"
                : hasMatch
                  ? "bg-zinc-800 text-zinc-500"
                  : "bg-zinc-800/50 text-zinc-700"
            }`}>
              {ag.count.toLocaleString()}
            </span>
          </button>
        );
      })}
    </aside>
  );
}
