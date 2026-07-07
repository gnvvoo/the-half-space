export function ArticleView({
  title,
  body,
  quote,
}: {
  title: string;
  body: string[];
  quote?: string;
}) {
  return (
    <article className="font-serif">
      <h1 className="text-[26px] font-extrabold leading-snug text-ink">{title}</h1>
      <div className="mt-6 flex flex-col gap-5">
        {body.map((paragraph, i) => (
          <p key={i} className="text-[17px] leading-[1.9] text-ink/90">
            {paragraph}
          </p>
        ))}
        {quote && (
          <blockquote className="border-l-2 border-brand pl-4 text-[17px] italic leading-[1.9] text-ink/80">
            {quote}
          </blockquote>
        )}
      </div>
    </article>
  );
}
